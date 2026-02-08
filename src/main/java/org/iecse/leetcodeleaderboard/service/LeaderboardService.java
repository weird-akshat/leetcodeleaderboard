package org.iecse.leetcodeleaderboard.service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.dto.UserData;
import org.iecse.leetcodeleaderboard.entity.LeetcodeUserId;
import org.iecse.leetcodeleaderboard.entity.UserProfile;
import org.iecse.leetcodeleaderboard.mapper.UserDataMapper;
import org.iecse.leetcodeleaderboard.repo.LeetcodeUserIdRepo;
import org.iecse.leetcodeleaderboard.repo.UserProfileRepo;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;


@Data
@Slf4j
@AllArgsConstructor
@Service
public class LeaderboardService {
    private final HttpGraphQlClient leetcodeClient;
    private final LeetcodeUserIdRepo leetcodeUserIdRepo;
    private final UserProfileRepo userProfileRepo;



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

    public Flux<UserData> getAllProfilesDetails()  {
        return fetchAllLeetcodeIdsFromDatabase().delayElements(Duration.ofSeconds(5)).flatMap(item-> this.getIdData(item.getUserId()));
    }

    public void updateAllProfiles(){
        getAllProfilesDetails().flatMap(userData ->
             userProfileRepo.findByLeetcodeId(userData.getUserName())
                    .map(userProfile->UserDataMapper.toUserProfile(userData,userProfile))
                    .switchIfEmpty(Mono.defer(()->Mono.just(UserDataMapper.toUserProfile(userData))))
                    .flatMap(userProfileRepo::save)

        ).subscribe();
    }
    public Flux<LeetcodeUserId> fetchAllLeetcodeIdsFromDatabase(){
        return leetcodeUserIdRepo.findAll();
    }



}
