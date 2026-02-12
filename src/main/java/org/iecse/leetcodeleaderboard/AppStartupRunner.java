package org.iecse.leetcodeleaderboard;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.repo.CurrentUserProfileStateRepo;
import org.iecse.leetcodeleaderboard.repo.MonthlyUserProfileStateRepo;
import org.iecse.leetcodeleaderboard.service.LeaderboardService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@AllArgsConstructor
public class AppStartupRunner implements CommandLineRunner {
    private final LeaderboardService leaderboardService;
    private final CurrentUserProfileStateRepo currentUserProfileStateRepo;
    private final MonthlyUserProfileStateRepo dailyUserProfileStateRepo;
    @Override
    public void run(String... args) throws Exception {
        currentUserProfileStateRepo.findTopRanked(1,1.25,1.5).doOnNext(
                userProfile -> System.out.println(userProfile)
        ).subscribe();

         leaderboardService.updateAllProfiles();
    }

}