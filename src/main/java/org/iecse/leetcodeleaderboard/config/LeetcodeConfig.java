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
