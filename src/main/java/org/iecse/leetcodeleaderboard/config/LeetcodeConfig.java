package org.iecse.leetcodeleaderboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class LeetcodeConfig {

    @Bean
    public HttpGraphQlClient leetcodeServiceClient(){
        WebClient webClient = WebClient.create("https://leetcode.com/graphql/");

        return HttpGraphQlClient.create(webClient);
    }


}
// {
//   "query": "\n    query userProfileUserQuestionProgressV2($userSlug: String!) {\n  userProfileUserQuestionProgressV2(userSlug: $userSlug) {\n    numAcceptedQuestions {\n      count\n      difficulty\n\n  }\n}\n    ",
//   "variables": {
//     "userSlug": "roonil03"
//   },
//   "operationName": "userProfileUserQuestionProgressV2"
// }

// {
//   "query": "query GetCountry($code: ID!) { country(code: $code) { name native capital emoji currency languages { code name } } }",
//   "variables": {
//     "code": "BR"
//   },
//   "operationName": "GetCountry"    // optional but recommended – matches your documentName
// }