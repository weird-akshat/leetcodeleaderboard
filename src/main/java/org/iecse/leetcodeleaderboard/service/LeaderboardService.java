package org.iecse.leetcodeleaderboard.service;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.catalina.User;
import org.iecse.leetcodeleaderboard.dto.UserData;
import org.iecse.leetcodeleaderboard.entity.LeetcodeUserId;
import org.iecse.leetcodeleaderboard.repo.LeetcodeUserIdRepo;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Service
public class LeaderboardService {
    private final HttpGraphQlClient leetcodeClient;
    private final LeetcodeUserIdRepo leetcodeUserIdRepo;



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

    public Flux<UserData> getProfiles(Flux<LeetcodeUserId> userIds)  {
       return userIds.delayElements(Duration.ofSeconds(5)).flatMap(item->{
           return this.getIdData(item.getUserId());
       });
    }

    public Flux<UserData> getProfilesFromDatabase(){
        Flux<LeetcodeUserId> leetcodeUserIds = leetcodeUserIdRepo.findAll();

        return this.getProfiles(leetcodeUserIds);
    }

}
