package org.iecse.leetcodeleaderboard;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.repo.CurrentUserProfileStateRepo;
import org.iecse.leetcodeleaderboard.repo.MonthlyUserProfileStateRepo;
import org.iecse.leetcodeleaderboard.security.exception.UserNotFoundException;
import org.iecse.leetcodeleaderboard.service.LeaderboardService;
import org.iecse.leetcodeleaderboard.service.MailService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@AllArgsConstructor
public class AppStartupRunner implements CommandLineRunner {
    private final LeaderboardService leaderboardService;
    private final CurrentUserProfileStateRepo currentUserProfileStateRepo;
    private final MonthlyUserProfileStateRepo dailyUserProfileStateRepo;
    private final MailService mailService;
    @Override
    public void run(String... args) throws Exception {
//        leaderboardService.getUserAboutMe("its_akshat").doOnNext(abc-> log.info("{}",abc))
//                .onErrorMap(throwable -> {
//                    log.error("Failed to fetch data for user: {}", throwable.getMessage());
//                    return new UserNotFoundException("Could not retrieve LeetCode profile for this user");
//                }).subscribe();
//        currentUserProfileStateRepo.findTopRanked(1,1.25,1.5).doOnNext(
//                userProfile -> System.out.println(userProfile)
//        ).subscribe();

//        leaderboardService.verifyLeetcodeId("its_akshat","thatweirdakshat@gmail.com").doOnNext(System.out::println).subscribe();
//        leaderboardService.getUserAboutMe("adityasinha347").doOnNext(System.out::println).subscribe() ;
//         mailService.sendPlainText("thatweirdakshat@gmail.com","Hey"," well");
//         leaderboardService.updateAllProfiles();
        leaderboardService.scheduledMonthlySync();
        leaderboardService.scheduledDailySync();
        leaderboardService.scheduledWeeklySync();
        leaderboardService.scheduledUpdate();
         return ;


    }

}