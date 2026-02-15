package org.iecse.leetcodeleaderboard.service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.dto.UserData;
import org.iecse.leetcodeleaderboard.dto.UserProfileDto;
import org.iecse.leetcodeleaderboard.entity.LeetcodeUserId;
import org.iecse.leetcodeleaderboard.mapper.UserDataMapper;
import org.iecse.leetcodeleaderboard.mapper.UserProfileMapper;
import org.iecse.leetcodeleaderboard.repo.*;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;


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
        return this.getUserAboutMe(leetcodeId).map(aboutMe->aboutMe.toLowerCase().contains("hello "+email.substring(0,email.indexOf('@'))));
    }

    public Flux<UserData> getAllProfilesDetails()  {
        return fetchAllLeetcodeIdsFromDatabase().delayElements(Duration.ofSeconds(5)).flatMap(item-> this.getIdData(item.getUserId()));
    }

    @Scheduled(cron = "@hourly")
    public void scheduledUpdate() {
        log.info("Starting scheduled profile update...");
        updateAllProfiles();
    }
    public void updateAllProfiles(){
        getAllProfilesDetails().flatMap(userData ->
             currentUserProfileStateRepo.findByLeetcodeId(userData.getUserName())
                    .map(userProfileState ->UserDataMapper.toUserProfile(userData, userProfileState))
                    .switchIfEmpty(Mono.defer(()->Mono.just(UserDataMapper.toUserProfile(userData))))
                    .flatMap(currentUserProfileStateRepo::save)
                     .onErrorResume(e -> {
                         log.error("Error updating user {}: {}", userData.getUserName(), e.getMessage());
                         return Mono.empty();
                     })

        ).subscribe();
    }
    public Flux<LeetcodeUserId> fetchAllLeetcodeIdsFromDatabase(){
        return leetcodeUserIdRepo.findAll();
    }

    public Flux<UserProfileDto> fetchLeaderboard(int easyMultiplier,int mediumMultiplier, int hardMultiplier){
        return currentUserProfileStateRepo.findTopRanked(easyMultiplier,mediumMultiplier,hardMultiplier).map(UserProfileMapper::userProfileToDto);
    }

    public Flux<UserProfileDto> fetchDailyLeaderboard(int easyMultiplier, int mediumMultiplier, int hardMultiplier){
        log.info("Finding: Daily Leaderboard");

        return dailyRepo.getDailyGainsLeaderboard(easyMultiplier,mediumMultiplier,hardMultiplier).map(UserProfileMapper::userProfileToDto);
    }

    public Flux<UserProfileDto> fetchWeeklyLeaderboard(int easyMultiplier, int mediumMultiplier, int hardMultiplier){
        return weeklyRepo.getWeeklyGainsLeaderboard(easyMultiplier,mediumMultiplier,hardMultiplier).map(UserProfileMapper::userProfileToDto);
    }

    public Flux<UserProfileDto> fetchMonthlyLeaderboard(int easyMultiplier, int mediumMultiplier, int hardMultiplier){
        return monthlyRepo.getMonthlyGainsLeaderboard(easyMultiplier,mediumMultiplier,hardMultiplier).map(UserProfileMapper::userProfileToDto);
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
