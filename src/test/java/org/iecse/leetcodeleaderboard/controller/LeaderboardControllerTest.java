package org.iecse.leetcodeleaderboard.controller;

import org.iecse.leetcodeleaderboard.dto.UpdateLeetcodeIdDto;
import org.iecse.leetcodeleaderboard.dto.UserProfileDto;
import org.iecse.leetcodeleaderboard.security.dto.LoginResponse;
import org.iecse.leetcodeleaderboard.service.LeaderboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderboardControllerTest {

    @Mock
    private LeaderboardService leaderboardService;

    @InjectMocks
    private LeaderboardController leaderboardController;

    @Test
    void getRankedLeaderboardShouldDelegateToService() {
        UserProfileDto dto = UserProfileDto.builder().leetcodeId("user1").easy(10).medium(20).hard(30).build();
        when(leaderboardService.fetchLeaderboard(1, 2, 3)).thenReturn(Flux.just(dto));

        StepVerifier.create(leaderboardController.getRankedLeaderboard(1, 2, 3))
                .expectNext(dto)
                .verifyComplete();

        verify(leaderboardService).fetchLeaderboard(1, 2, 3);
    }

    @Test
    void getDailyRankedLeaderboardShouldDelegateToService() {
        UserProfileDto dto = UserProfileDto.builder().leetcodeId("daily").easy(1).medium(1).hard(1).build();
        when(leaderboardService.fetchDailyLeaderboard(4, 5, 6)).thenReturn(Flux.just(dto));

        StepVerifier.create(leaderboardController.getDailyRankedLeaderboard(4, 5, 6))
                .expectNext(dto)
                .verifyComplete();

        verify(leaderboardService).fetchDailyLeaderboard(4, 5, 6);
    }

    @Test
    void getWeeklyRankedLeaderboardShouldDelegateToService() {
        UserProfileDto dto = UserProfileDto.builder().leetcodeId("weekly").easy(2).medium(3).hard(4).build();
        when(leaderboardService.fetchWeeklyLeaderboard(3, 2, 1)).thenReturn(Flux.just(dto));

        StepVerifier.create(leaderboardController.getWeeklyRankedLeaderboard(3, 2, 1))
                .expectNext(dto)
                .verifyComplete();

        verify(leaderboardService).fetchWeeklyLeaderboard(3, 2, 1);
    }

    @Test
    void getMonthlyRankedLeaderboardShouldDelegateToService() {
        UserProfileDto dto = UserProfileDto.builder().leetcodeId("monthly").easy(7).medium(8).hard(9).build();
        when(leaderboardService.fetchMonthlyLeaderboard(10, 11, 12)).thenReturn(Flux.just(dto));

        StepVerifier.create(leaderboardController.getMonthlyRankedLeaderboard(10, 11, 12))
                .expectNext(dto)
                .verifyComplete();

        verify(leaderboardService).fetchMonthlyLeaderboard(10, 11, 12);
    }

    @Test
    void updateLeetcodeIdShouldWrapTokenInResponseEntity() {
        UpdateLeetcodeIdDto request = UpdateLeetcodeIdDto.builder().newLeetcodeId("new-id").build();
        when(leaderboardService.updateLeetcodeIdUser("new-id")).thenReturn(Mono.just("jwt-token"));

        StepVerifier.create(leaderboardController.updateLeetcodeId(request))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    LoginResponse body = response.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.getToken()).isEqualTo("jwt-token");
                    assertThat(body.getType()).isEqualTo("Bearer");
                })
                .verifyComplete();

        verify(leaderboardService).updateLeetcodeIdUser("new-id");
    }
}
