package org.iecse.leetcodeleaderboard.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.dto.UpdateLeetcodeIdDto;
import org.iecse.leetcodeleaderboard.dto.UserProfileDto;
import org.iecse.leetcodeleaderboard.security.dto.LoginResponse;
import org.iecse.leetcodeleaderboard.service.LeaderboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @PutMapping("/updateLeetcodeId")
    public Mono<ResponseEntity<LoginResponse>> updateLeetcodeId(@RequestBody UpdateLeetcodeIdDto updateLeetcodeIdDto){
        log.info("Hit the endpoint");
        return leaderboardService.updateLeetcodeIdUser(updateLeetcodeIdDto.getNewLeetcodeId()).map(LoginResponse::new).map(loginResponse->new ResponseEntity<>(loginResponse, HttpStatus.OK));
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
