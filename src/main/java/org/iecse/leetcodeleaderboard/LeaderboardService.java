package org.iecse.leetcodeleaderboard.services;
import org.iecse.leetcodeleaderboard.dto.ProgressData;

import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
@Service
public class LeaderboardService {
    private final HttpGraphQlClient leetcodeClient;
    public LeaderboardService(HttpGraphQlClient leetcodClient){
        this.leetcodeClient= leetcodClient;
    }
    public ProgressData getIdData(String username){
      System.out.println("HEYYYY!!!!");
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
            .toEntity(ProgressData.class)
            .block();
    }
    
}
