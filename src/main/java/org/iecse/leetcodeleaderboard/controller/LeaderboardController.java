package org.iecse.leetcodeleaderboard.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.dto.UserProfileDto;
import org.iecse.leetcodeleaderboard.service.LeaderboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
@Slf4j
@RestController
@RequestMapping("/api/v1/leaderboard")
@AllArgsConstructor
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    private static final String DEFAULT_EASY = "100";
    private static final String DEFAULT_MEDIUM = "125";
    private static final String DEFAULT_HARD = "150";

    @GetMapping
    public Flux<UserProfileDto> getRankedLeaderboard(
            @RequestParam(defaultValue = DEFAULT_EASY) int easy,
            @RequestParam(defaultValue = DEFAULT_MEDIUM) int medium,
            @RequestParam(defaultValue = DEFAULT_HARD) int hard) {

        return leaderboardService.fetchLeaderboard(easy, medium, hard);
    }

    @GetMapping("/daily")
    public Flux<UserProfileDto> getDailyRankedLeaderboard(
            @RequestParam(defaultValue = DEFAULT_EASY) int easy,
            @RequestParam(defaultValue = DEFAULT_MEDIUM) int medium,
            @RequestParam(defaultValue = DEFAULT_HARD) int hard) {

        log.info("Request Received: Daily Leaderboard");
        return leaderboardService.fetchDailyLeaderboard(easy, medium, hard);
    }

    @GetMapping("/weekly")
    public Flux<UserProfileDto> getWeeklyRankedLeaderboard(
            @RequestParam(defaultValue = DEFAULT_EASY) int easy,
            @RequestParam(defaultValue = DEFAULT_MEDIUM) int medium,
            @RequestParam(defaultValue = DEFAULT_HARD) int hard) {

        return leaderboardService.fetchWeeklyLeaderboard(easy, medium, hard);
    }

    @GetMapping("/monthly")
    public Flux<UserProfileDto> getMonthlyRankedLeaderboard(
            @RequestParam(defaultValue = DEFAULT_EASY) int easy,
            @RequestParam(defaultValue = DEFAULT_MEDIUM) int medium,
            @RequestParam(defaultValue = DEFAULT_HARD) int hard) {

        return leaderboardService.fetchMonthlyLeaderboard(easy, medium, hard);
    }

}
