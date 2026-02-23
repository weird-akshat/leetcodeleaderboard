package org.iecse.leetcodeleaderboard.service;

import org.iecse.leetcodeleaderboard.repo.DailyUserProfileStateRepo;
import org.iecse.leetcodeleaderboard.repo.MonthlyUserProfileStateRepo;
import org.iecse.leetcodeleaderboard.repo.WeeklyUserProfileStateRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderboardSyncServiceTest {

    @Mock
    private DailyUserProfileStateRepo dailyRepo;

    @Mock
    private WeeklyUserProfileStateRepo weeklyRepo;

    @Mock
    private MonthlyUserProfileStateRepo monthlyRepo;

    @Test
    void dailySyncShouldClearThenSync() {
        LeaderboardSyncService service = new LeaderboardSyncService(dailyRepo, weeklyRepo, monthlyRepo);
        when(dailyRepo.clearTable()).thenReturn(Mono.empty());
        when(dailyRepo.syncFromCurrent()).thenReturn(Mono.empty());

        StepVerifier.create(service.dailySync()).verifyComplete();

        InOrder order = inOrder(dailyRepo);
        order.verify(dailyRepo).clearTable();
        order.verify(dailyRepo).syncFromCurrent();
    }

    @Test
    void weeklySyncShouldClearThenSync() {
        LeaderboardSyncService service = new LeaderboardSyncService(dailyRepo, weeklyRepo, monthlyRepo);
        when(weeklyRepo.clearTable()).thenReturn(Mono.empty());
        when(weeklyRepo.syncFromCurrent()).thenReturn(Mono.empty());

        StepVerifier.create(service.weeklySync()).verifyComplete();

        InOrder order = inOrder(weeklyRepo);
        order.verify(weeklyRepo).clearTable();
        order.verify(weeklyRepo).syncFromCurrent();
    }

    @Test
    void monthlySyncShouldClearThenSync() {
        LeaderboardSyncService service = new LeaderboardSyncService(dailyRepo, weeklyRepo, monthlyRepo);
        when(monthlyRepo.clearTable()).thenReturn(Mono.empty());
        when(monthlyRepo.syncFromCurrent()).thenReturn(Mono.empty());

        StepVerifier.create(service.monthlySync()).verifyComplete();

        InOrder order = inOrder(monthlyRepo);
        order.verify(monthlyRepo).clearTable();
        order.verify(monthlyRepo).syncFromCurrent();
    }
}
