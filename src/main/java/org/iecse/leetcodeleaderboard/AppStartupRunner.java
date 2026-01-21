package org.iecse.leetcodeleaderboard;

import org.iecse.leetcodeleaderboard.services.LeaderboardService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements CommandLineRunner {
    
    public final LeaderboardService leaderboardService;
    AppStartupRunner(LeaderboardService leaderboardService){
        this.leaderboardService=leaderboardService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(leaderboardService.getIdData("its_akshat").getNumAcceptedQuestions());
    }
}