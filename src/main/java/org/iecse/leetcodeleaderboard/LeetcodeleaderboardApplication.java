package org.iecse.leetcodeleaderboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeetcodeleaderboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeetcodeleaderboardApplication.class, args);
	}

}

//the purpose of this project is to build a leetcode ranking application for my juniors to promote leetcode culture inside the club.
//i have built a method to get the leetcode data of a single user
//now we need to build something that takes a list and then gets the data from leetcode