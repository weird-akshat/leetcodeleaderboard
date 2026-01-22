package org.iecse.leetcodeleaderboard;

import org.iecse.leetcodeleaderboard.dto.UserData;
import org.iecse.leetcodeleaderboard.service.LeaderboardService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppStartupRunner implements CommandLineRunner {

    public final LeaderboardService leaderboardService;
    AppStartupRunner(LeaderboardService leaderboardService){
        this.leaderboardService=leaderboardService;
    }
    List<String> list= new ArrayList<>();
    @Override
    public void run(String... args) throws Exception {

        Flux<UserData> users= leaderboardService.getProfilesFromDatabase();
        users
                .doOnNext(user -> System.out.println("Received: " + user))
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .blockLast();
        }
}