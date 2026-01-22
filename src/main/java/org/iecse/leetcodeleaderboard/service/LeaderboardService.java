package org.iecse.leetcodeleaderboard.service;
import org.iecse.leetcodeleaderboard.dto.UserData;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
public class LeaderboardService {
    private final HttpGraphQlClient leetcodeClient;


    public LeaderboardService(HttpGraphQlClient leetcodeClient){
        this.leetcodeClient= leetcodeClient;
    }


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

    public Flux<UserData> getProfiles(List<String> userIds)  {
        return Flux.fromIterable(userIds)
                .delayElements(Duration.ofSeconds(4))
                .flatMap(this::getIdData);
    }
    
}
