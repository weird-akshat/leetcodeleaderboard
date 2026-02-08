package org.iecse.leetcodeleaderboard;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.service.LeaderboardService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@AllArgsConstructor
public class AppStartupRunner implements CommandLineRunner {
    public final LeaderboardService leaderboardService;
    @Override
    public void run(String... args) throws Exception {
         leaderboardService.updateAllProfiles();
    }

}