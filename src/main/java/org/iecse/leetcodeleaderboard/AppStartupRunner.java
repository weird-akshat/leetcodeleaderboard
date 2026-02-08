package org.iecse.leetcodeleaderboard;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.dto.UserData;
import org.iecse.leetcodeleaderboard.repo.UserProfileRepo;
import org.iecse.leetcodeleaderboard.service.LeaderboardService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
@AllArgsConstructor
public class AppStartupRunner implements CommandLineRunner {
    public final UserProfileRepo userProfileRepo;
    public final LeaderboardService leaderboardService;

    List<String> list= new ArrayList<>();
    @Override
    public void run(String... args) throws Exception {

         leaderboardService.updateAllProfiles();
    }

}