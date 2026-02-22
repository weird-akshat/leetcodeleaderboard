package org.iecse.leetcodeleaderboard.service;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.dto.UserData;
import org.iecse.leetcodeleaderboard.dto.UserProfileDto;
import org.iecse.leetcodeleaderboard.entity.LeetcodeUserId;
import org.iecse.leetcodeleaderboard.mapper.UserDataMapper;
import org.iecse.leetcodeleaderboard.mapper.UserProfileMapper;
import org.iecse.leetcodeleaderboard.repo.*;
import org.iecse.leetcodeleaderboard.security.entity.AppUser;
import org.iecse.leetcodeleaderboard.security.jwt.JwtTokenProvider;
import org.iecse.leetcodeleaderboard.security.repo.AppUserRepository;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


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
            .toEntity(UserData.class).map(userData->{userData.setUserName(username); return userData;});




    }
    public Mono<String> getUserAboutMe(String username) {
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
                });
    }
    public Mono<Boolean> verifyLeetcodeId(String leetcodeId, String email){
        return this.getUserAboutMe(leetcodeId).map(aboutMe->{
            log.info("verifying leetcodeId: {}",leetcodeId);
            return aboutMe.toLowerCase().contains("hello "+email.substring(0,email.indexOf('@')));
        });
    }

    public Flux<UserData> getAllProfilesDetails()  {
        return leetcodeUserIdRepo.findAll().delayElements(Duration.ofSeconds(5)).flatMap(item->{
            log.info("Id: {}",item);
            return this.getIdData(item.getUserId());
        } );
    }

    @Scheduled(cron = "@hourly")
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
                        return currentUserProfileStateRepo.save(currentUserProfileState);
                    }
                    else{
                        return Mono.just(currentUserProfileState);
                    }
                });
            }
        }

        ).subscribe();
    }

    public Mono<Boolean> isLeetcodeIdActive(){
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
                .map(auth->auth.getName())
                .flatMap(username->appUserRepo.findByUsername(username))
                .map(appUser -> appUser.getLeetcodeId())
                .flatMap(leetocodeId->currentUserProfileStateRepo.findByLeetcodeId(leetocodeId))
                        .map(currentUserProfileState -> currentUserProfileState.isActive())
                .flatMap(bool->Mono.just(bool)
                );

    }
    public Flux<UserProfileDto> fetchLeaderboard(int easyMultiplier,int mediumMultiplier, int hardMultiplier){
        return isLeetcodeIdActive()
                .filter(isActive -> isActive)
                .switchIfEmpty(Mono.error(new RuntimeException("Your LeetCode id's details can't be found, please correct your leetcode id")))
                .flatMapMany(isActive -> currentUserProfileStateRepo.findTopRanked(easyMultiplier,mediumMultiplier,hardMultiplier).map(UserProfileMapper::userProfileToDto));
    }

    public Flux<UserProfileDto> fetchDailyLeaderboard(int easyMultiplier, int mediumMultiplier, int hardMultiplier){
        log.info("Finding: Daily Leaderboard");

        return isLeetcodeIdActive()
                .filter(isActive -> isActive)
                .switchIfEmpty(Mono.error(new RuntimeException("Your LeetCode id's details can't be found, please correct your leetcode id")))
                .flatMapMany(isActive ->dailyRepo.getDailyGainsLeaderboard(easyMultiplier,mediumMultiplier,hardMultiplier).map(UserProfileMapper::userProfileToDto));
    }

    public Flux<UserProfileDto> fetchWeeklyLeaderboard(int easyMultiplier, int mediumMultiplier, int hardMultiplier){
        return isLeetcodeIdActive()
                .filter(isActive -> isActive)
                .switchIfEmpty(Mono.error(new RuntimeException("Your LeetCode id's details can't be found, please correct your leetcode id")))
                .flatMapMany(isActive ->weeklyRepo.getWeeklyGainsLeaderboard(easyMultiplier,mediumMultiplier,hardMultiplier).map(UserProfileMapper::userProfileToDto));
    }

    public Flux<UserProfileDto> fetchMonthlyLeaderboard(int easyMultiplier, int mediumMultiplier, int hardMultiplier){
        return isLeetcodeIdActive()
                .filter(isActive -> isActive)
                .switchIfEmpty(Mono.error(new RuntimeException("Your LeetCode id's details can't be found, please correct your leetcode id")))
                .flatMapMany(isActive ->monthlyRepo.getMonthlyGainsLeaderboard(easyMultiplier,mediumMultiplier,hardMultiplier).map(UserProfileMapper::userProfileToDto));
    }
    private Mono<AppUser> changeUserLeetcodeId(AppUser appUser, String newLeetcodeId){
        appUser.setLeetcodeId(newLeetcodeId);
        return appUserRepo.save(appUser);
    }
    @Transactional
    public Mono<String> updateLeetcodeIdUser(String newLeetcodeId){
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
                .map(auth->auth.getName())
                .flatMap(email-> this.verifyLeetcodeId(newLeetcodeId,email).filter(Boolean::booleanValue).switchIfEmpty(Mono.error(new RuntimeException("Leetcode Id not verified"))))
                .then(ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication))
                .map(authentication -> authentication.getName())
                .flatMap(username->appUserRepo.findByUsername(username))
                .flatMap(appUser -> {
                    log.info("Update: {}",appUser);
                    return updateLeetId(appUser.getLeetcodeId(), newLeetcodeId, appUser.getUsername()).then(
                        Mono.defer(()->changeUserLeetcodeId(appUser,newLeetcodeId))

                    );
                })
                .flatMap(appUser -> Mono.just(jwtTokenProvider.createToken(appUser.getUsername(),appUser.getRole(), appUser.getLeetcodeId())));
    }
    public Mono<Void> changeIdiInLeetcodeUserIds(String oldLeetcodeId, String newLeetcodeId){
        LeetcodeUserId leetcodeUserId = new LeetcodeUserId(newLeetcodeId);
        return leetcodeUserIdRepo.deleteById(oldLeetcodeId).then(leetcodeUserIdRepo.insertUser(leetcodeUserId));
    }
    public Mono<Void> updateLeetId(String oldLeetcodeId, String newLeetcodeId, String email){
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
        return currentUserProfileStateRepo.findByLeetcodeId(oldLeetcodeId).flatMap(currentUserProfileState -> {
            currentUserProfileState.setLeetcodeId(newLeetcodeId);
            return currentUserProfileStateRepo.save(currentUserProfileState);
        }).map(currentUserProfileState -> {
            if (currentUserProfileState.getLeetcodeId().equals(newLeetcodeId)){
                return true;
            }
            else{
                throw new RuntimeException();
            }
        });
    }

    public Mono<Boolean> changeIdInDailyState(String oldLeetcodeId,String newLeetcodeId){
        return dailyRepo.findByLeetcodeId(oldLeetcodeId).flatMap(dailyUserProfileState -> {
            dailyUserProfileState.setLeetcodeId(newLeetcodeId);
            return dailyRepo.save(dailyUserProfileState);
        }).map(currentUserProfileState -> {
            if (currentUserProfileState.getLeetcodeId().equals(newLeetcodeId)){
                return true;
            }
            else{
                throw new RuntimeException();
            }
        });
    }
    public Mono<Boolean> changeIdInMonthlyState(String oldLeetcodeId,String newLeetcodeId){
        return monthlyRepo.findByLeetcodeId(oldLeetcodeId).flatMap(monthlyUserProfileState -> {
            monthlyUserProfileState.setLeetcodeId(newLeetcodeId);
            return monthlyRepo.save(monthlyUserProfileState);
        }).map(currentUserProfileState -> {
            if (currentUserProfileState.getLeetcodeId().equals(newLeetcodeId)){
                return true;
            }
            else{
                throw new RuntimeException();
            }
        });
    }
    public Mono<Boolean> changeIdInWeekly(String oldLeetcodeId,String newLeetcodeId){
        return weeklyRepo.findByLeetcodeId(oldLeetcodeId).flatMap(weeklyUserProfileState -> {
            weeklyUserProfileState.setLeetcodeId(newLeetcodeId);
            return weeklyRepo.save(weeklyUserProfileState);
        }).map(currentUserProfileState -> {
            if (currentUserProfileState.getLeetcodeId().equals(newLeetcodeId)){
                return true;
            }
            else{
                throw new RuntimeException();
            }
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
