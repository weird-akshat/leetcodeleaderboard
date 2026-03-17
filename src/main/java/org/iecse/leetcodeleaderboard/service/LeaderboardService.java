package org.iecse.leetcodeleaderboard.service;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.dto.UserData;
import org.iecse.leetcodeleaderboard.dto.UserProfileDto;
import org.iecse.leetcodeleaderboard.entity.CurrentUserProfileState;
import org.iecse.leetcodeleaderboard.entity.LeetcodeUserId;
import org.iecse.leetcodeleaderboard.exception.*;
import org.iecse.leetcodeleaderboard.mapper.UserDataMapper;
import org.iecse.leetcodeleaderboard.mapper.UserProfileMapper;
import org.iecse.leetcodeleaderboard.repo.*;
import org.iecse.leetcodeleaderboard.security.entity.AppUser;
import org.iecse.leetcodeleaderboard.security.jwt.JwtTokenProvider;
import org.iecse.leetcodeleaderboard.security.repo.AppUserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.graphql.client.GraphQlClientException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.core.codec.DecodingException;



@Data
@Slf4j
@AllArgsConstructor
@Service
public class LeaderboardService {
    private final HttpGraphQlClient leetcodeClient;
    private final LeetcodeUserIdRepo leetcodeUserIdRepo;
    private final CurrentUserProfileStateRepo currentUserProfileStateRepo;
    private final DailyUserProfileStateRepo dailyRepo;
    private final WeeklyUserProfileStateRepo weeklyRepo;
    private final LeaderboardSyncService leaderboardSyncService;
    private final MonthlyUserProfileStateRepo monthlyRepo;
    private final AppUserRepository appUserRepo;
    private final JwtTokenProvider jwtTokenProvider;



    public Mono<UserData> getIdData(String username){
        log.trace("Fetching leet-code account details for user: {}",username);
        String query = """
            query userProfileUserQuestionProgressV2($userSlug: String!) {
              userProfileUserQuestionProgressV2(userSlug: $userSlug) {
                numAcceptedQuestions {
                  count
                  difficulty
                }

              }
            }
            """;
        return leetcodeClient.document(query)
            .variable("userSlug", username)
            .retrieve("userProfileUserQuestionProgressV2")
            .toEntity(UserData.class).map(userData->{userData.setUserName(username); return userData;})
                .onErrorMap(WebClientRequestException.class,
                        ex -> new LeetcodeAPIException(
                                "LeetCode service unreachable", ex))

                .onErrorMap(WebClientResponseException.class,
                        ex -> new LeetcodeAPIException(
                                "LeetCode API returned HTTP error: " + ex.getStatusCode(), ex))

                .onErrorMap(GraphQlClientException.class,
                        ex -> new LeetcodeAPIException(
                                "LeetCode GraphQL error", ex))

                .onErrorMap(DecodingException.class,
                        ex -> new LeetcodeAPIException(
                                "Failed to decode LeetCode response", ex));

    }
    public Mono<String> getUserAboutMe(String username) {
        log.trace("Fetching leet-code about-me details for user: {}",username);
        String query = """
        query userPublicProfile($username: String!) {
          matchedUser(username: $username) {
            profile {
              aboutMe
            }
          }
        }
        """;

        return leetcodeClient.document(query)
                .variable("username", username)
                .retrieve("matchedUser")
                .toEntity(JsonNode.class)
                .map(node -> {
                    return node.path("profile").path("aboutMe").asText();
                })
                .onErrorMap(WebClientRequestException.class,
                        ex -> new LeetcodeAPIException(
                                "LeetCode service unreachable", ex))

                .onErrorMap(WebClientResponseException.class,
                        ex -> new LeetcodeAPIException(
                                "LeetCode API returned HTTP error: " + ex.getStatusCode(), ex))

                .onErrorMap(GraphQlClientException.class,
                        ex -> new LeetcodeAPIException(
                                "LeetCode GraphQL error", ex))

                .onErrorMap(DecodingException.class,
                        ex -> new LeetcodeAPIException(
                                "Failed to decode LeetCode response", ex));
    }
    public Mono<Boolean> verifyLeetcodeId(String leetcodeId, String email){
        log.debug("Verifying leetcodeId {} for {}",leetcodeId,email);
        return this.getUserAboutMe(leetcodeId).map(aboutMe->{
            log.info("verifying leetcodeId: {}",leetcodeId);
            if (aboutMe.toLowerCase().contains("hello "+email.substring(0,email.indexOf('@')))){
                return true;
            }
            else{
                throw new LeetcodeIdNotVerifiedException();
            }
        });
    }

    public Flux<UserData> getAllProfilesDetails()  {
        return leetcodeUserIdRepo.findAll().delayElements(Duration.ofSeconds(5)).flatMap(item->{
            log.info("Id: {}",item);
            return this.getIdData(item.getUserId())
                    .onErrorResume(e -> {
                        log.error("Failed to fetch data for user {}: {}", item.getUserId(), e.getMessage());
                        return Mono.empty();
                    }).onErrorMap(DataAccessException.class, e ->
                            new DatabaseOperationException("Database error while fetching users", e)
                    );
        });
    }

    @Scheduled(cron = "@daily")
    public void scheduledUpdate() {
        log.info("Starting scheduled profile update...");
        updateAllProfiles();
    }
    public void updateAllProfiles(){
        getAllProfilesDetails().flatMap(userData ->{
            if (!userData.getNumAcceptedQuestions().isEmpty()) {

                return currentUserProfileStateRepo.findByLeetcodeId(userData.getUserName())
                        .map(userProfileState -> {
                            log.info("Updating leetcodeId: {}", userProfileState.getLeetcodeId());
                            return UserDataMapper.toUserProfile(userData, userProfileState);
                        })
                        .switchIfEmpty(Mono.defer(() -> Mono.just(UserDataMapper.toUserProfile(userData))))
                        .flatMap(currentUserProfileStateRepo::save)
                        .onErrorResume(e -> {
                            log.error("Error updating user {}: {}", userData.getUserName(), e.getMessage());
                            return Mono.empty();
                        });
            }
            else{
                return currentUserProfileStateRepo.findByLeetcodeId(userData.getUserName()).flatMap(currentUserProfileState->{
                    if (ChronoUnit.DAYS.between(currentUserProfileState.getLastUpdated(),LocalDateTime.now())>=2){
                        currentUserProfileState.setActive(false);
                        return currentUserProfileStateRepo.save(currentUserProfileState)
                                .onErrorResume(e -> {
                                    log.error("Failed to update inactive status for user {}: {}", currentUserProfileState.getLeetcodeId(), e.getMessage());
                                    return Mono.empty(); // Swallow the error so the rest of the users still get updated
                                });
                    }
                    else{
                        return Mono.just(currentUserProfileState);
                    }
                });
            }
        }

        ).subscribe(
                null,
                error -> log.error("Critical error during scheduled update loop", error)
        );
    }

    public Mono<Boolean> isLeetcodeIdActive(){
        log.trace("Check: isLeetcodeIdActive");
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
                .map(Principal::getName)
                .flatMap(appUserRepo::findByUsername)
                .switchIfEmpty(Mono.error(new UserProfileNotFoundException("AppUser not found in context")))
                .map(AppUser::getLeetcodeId)
                .flatMap(currentUserProfileStateRepo::findByLeetcodeId)
                        .map(CurrentUserProfileState::isActive)
                .flatMap(Mono::just
                );

    }
    public Flux<UserProfileDto> fetchLeaderboard(int easyMultiplier,int mediumMultiplier, int hardMultiplier){
        return isLeetcodeIdActive()
                .filter(isActive -> isActive)
                .switchIfEmpty(Mono.error(new LeetcodeIdChangedException()))
                .flatMapMany(isActive -> currentUserProfileStateRepo.findTopRanked(easyMultiplier,mediumMultiplier,hardMultiplier).map(UserProfileMapper::userProfileToDto));
    }

    public Flux<UserProfileDto> fetchDailyLeaderboard(int easyMultiplier, int mediumMultiplier, int hardMultiplier){
        log.info("Finding: Daily Leaderboard");

        return isLeetcodeIdActive()
                .filter(isActive -> isActive)
                .switchIfEmpty(Mono.error(new LeetcodeIdChangedException()))
                .flatMapMany(isActive ->dailyRepo.getDailyGainsLeaderboard(easyMultiplier,mediumMultiplier,hardMultiplier).map(UserProfileMapper::userProfileToDto));
    }

    public Flux<UserProfileDto> fetchWeeklyLeaderboard(int easyMultiplier, int mediumMultiplier, int hardMultiplier){
        return isLeetcodeIdActive()
                .filter(isActive -> isActive)
                .switchIfEmpty(Mono.error(new LeetcodeIdChangedException()))
                .flatMapMany(isActive ->weeklyRepo.getWeeklyGainsLeaderboard(easyMultiplier,mediumMultiplier,hardMultiplier).map(UserProfileMapper::userProfileToDto));
    }

    public Flux<UserProfileDto> fetchMonthlyLeaderboard(int easyMultiplier, int mediumMultiplier, int hardMultiplier){
        return isLeetcodeIdActive()
                .filter(isActive -> isActive)
                .switchIfEmpty(Mono.error(new LeetcodeIdChangedException()))
                .flatMapMany(isActive ->monthlyRepo.getMonthlyGainsLeaderboard(easyMultiplier,mediumMultiplier,hardMultiplier).map(UserProfileMapper::userProfileToDto));
    }
    private Mono<AppUser> changeUserLeetcodeId(AppUser appUser, String newLeetcodeId){
        appUser.setLeetcodeId(newLeetcodeId);
        return appUserRepo.save(appUser)
                .onErrorMap(DataAccessException.class, e ->
                        new DatabaseOperationException("Failed to update AppUser in database", e));
    }
    @Transactional
    public Mono<String> updateLeetcodeIdUser(String newLeetcodeId){
        return
                appUserRepo.findByLeetcodeId(newLeetcodeId).flatMap(appUser -> Mono.<String>error(new LeetcodeIdInUseException()))
                                .switchIfEmpty(
                                        ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
                                                .map(Principal::getName)
                                                .flatMap(email-> this.verifyLeetcodeId(newLeetcodeId,email).filter(Boolean::booleanValue).switchIfEmpty(Mono.error(new LeetcodeIdNotVerifiedException())))
                                                .then(ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication))
                                                .map(Principal::getName)
                                                .flatMap(appUserRepo::findByUsername)

                                                .flatMap(appUser -> {
                                                    log.info("Update: {}",appUser);
                                                    return updateLeetId(appUser.getLeetcodeId(), newLeetcodeId).then(
                                                            Mono.defer(()->changeUserLeetcodeId(appUser,newLeetcodeId))

                                                    );
                                                }).flatMap(appUser -> Mono.just(jwtTokenProvider.createToken(appUser.getUsername(),appUser.getRole(), appUser.getLeetcodeId())))
                                                .doOnError(e -> log.error("Transaction failed during LeetCode ID update, initiating rollback. Reason: {}", e.getMessage()))
                                )

                        ;

    }
    public Mono<Void> changeIdiInLeetcodeUserIds(String oldLeetcodeId, String newLeetcodeId){
        LeetcodeUserId leetcodeUserId = new LeetcodeUserId(newLeetcodeId);
        return leetcodeUserIdRepo.deleteById(oldLeetcodeId).onErrorResume(error->leetcodeUserIdRepo.insertUser(leetcodeUserId))
                .onErrorMap(DataAccessException.class, e ->
                        new DatabaseOperationException("Failed to swap old ID for new ID in leetcode_user_ids table", e));
    }
    public Mono<Void> updateLeetId(String oldLeetcodeId, String newLeetcodeId){
        log.info("Started updating leetcodeId");
        return Mono.when(
                changeIdiInLeetcodeUserIds(oldLeetcodeId,newLeetcodeId),
                changeIdInDailyState(oldLeetcodeId,newLeetcodeId),
                changeIdInWeekly(oldLeetcodeId,newLeetcodeId),
                changeIdInMonthlyState(oldLeetcodeId,newLeetcodeId),
                changeIdInCurrentState(oldLeetcodeId,newLeetcodeId)
        );

    }
    public Mono<Boolean> changeIdInCurrentState(String oldLeetcodeId, String newLeetcodeId){
        return currentUserProfileStateRepo.findByLeetcodeId(oldLeetcodeId).doOnNext(
                abc->log.info("{}",abc)
                )
                .switchIfEmpty(Mono.empty())
                .flatMap(currentUserProfileState -> {
            currentUserProfileState.setLeetcodeId(newLeetcodeId);
            return currentUserProfileStateRepo.save(currentUserProfileState)
                    .onErrorMap(DataAccessException.class, e ->
                            new DatabaseOperationException("Database error while updating state table", e));
        }).map(currentUserProfileState -> {
            if (currentUserProfileState.getLeetcodeId().equals(newLeetcodeId)){
                return true;
            }
            else{
                throw new LeetcodeIdUpdateException("Failed to verify ID update for: " + newLeetcodeId);            }
        });
    }

    public Mono<Boolean> changeIdInDailyState(String oldLeetcodeId,String newLeetcodeId){
        return dailyRepo.findByLeetcodeId(oldLeetcodeId)
                .switchIfEmpty(Mono.empty())
                .flatMap(dailyUserProfileState -> {
            dailyUserProfileState.setLeetcodeId(newLeetcodeId);
            return dailyRepo.save(dailyUserProfileState)
                    .onErrorMap(DataAccessException.class, e ->
                            new DatabaseOperationException("Database error while updating state table", e));
        }).map(currentUserProfileState -> {
            if (currentUserProfileState.getLeetcodeId().equals(newLeetcodeId)){
                return true;
            }
            else{
                throw new LeetcodeIdUpdateException("Failed to verify ID update for: " + newLeetcodeId);            }
        });
    }
    public Mono<Boolean> changeIdInMonthlyState(String oldLeetcodeId,String newLeetcodeId){
        return monthlyRepo.findByLeetcodeId(oldLeetcodeId)
                .switchIfEmpty(Mono.empty())
                .flatMap(monthlyUserProfileState -> {
            monthlyUserProfileState.setLeetcodeId(newLeetcodeId);
            return monthlyRepo.save(monthlyUserProfileState)
                    .onErrorMap(DataAccessException.class, e ->
                            new DatabaseOperationException("Database error while updating state table", e));
        }).map(currentUserProfileState -> {
            if (currentUserProfileState.getLeetcodeId().equals(newLeetcodeId)){
                return true;
            }
            else{
                throw new LeetcodeIdUpdateException("Failed to verify ID update for: " + newLeetcodeId);            }
        });
    }
    public Mono<Boolean> changeIdInWeekly(String oldLeetcodeId,String newLeetcodeId){
        return weeklyRepo.findByLeetcodeId(oldLeetcodeId)
                .switchIfEmpty(Mono.empty())
                .flatMap(weeklyUserProfileState -> {
            weeklyUserProfileState.setLeetcodeId(newLeetcodeId);
            return weeklyRepo.save(weeklyUserProfileState)
                    .onErrorMap(DataAccessException.class, e ->
                            new DatabaseOperationException("Database error while updating state table", e));
        }).map(currentUserProfileState -> {
            if (currentUserProfileState.getLeetcodeId().equals(newLeetcodeId)){
                return true;
            }
            else{
                throw new LeetcodeIdUpdateException("Failed to verify ID update for: " + newLeetcodeId);            }
        });
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledDailySync() {
        leaderboardSyncService.dailySync().block();
    }

    @Scheduled(cron = "0 0 0 * * SUN")
    public void scheduledWeeklySync() {
        leaderboardSyncService.weeklySync().block();
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void scheduledMonthlySync() {
        leaderboardSyncService.monthlySync().block();
    }




}
