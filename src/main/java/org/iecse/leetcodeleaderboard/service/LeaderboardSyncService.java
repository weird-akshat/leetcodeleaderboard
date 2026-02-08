package org.iecse.leetcodeleaderboard.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.repo.DailyUserProfileStateRepo;
import org.iecse.leetcodeleaderboard.repo.MonthlyUserProfileStateRepo;
import org.iecse.leetcodeleaderboard.repo.WeeklyUserProfileStateRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
@Service
@AllArgsConstructor
@Slf4j
public class LeaderboardSyncService {
    private final DailyUserProfileStateRepo dailyRepo;
    private final WeeklyUserProfileStateRepo weeklyRepo;
    private final MonthlyUserProfileStateRepo monthlyRepo;


    @Transactional
    public Mono<Void> dailySync() {
        log.info("Starting Daily Sync...");
        return dailyRepo.clearTable()
                .then(dailyRepo.syncFromCurrent())
                .doOnSuccess(v -> log.info("Daily Sync Complete"));
    }

    @Transactional
    public Mono<Void> weeklySync() {
        log.info("Starting Weekly Sync...");
        return weeklyRepo.clearTable()
                .then(weeklyRepo.syncFromCurrent())
                .doOnSuccess(v -> log.info("Weekly Sync Complete"));
    }

    @Transactional
    public Mono<Void> monthlySync() {
        log.info("Starting Monthly Sync...");
        return monthlyRepo.clearTable()
                .then(monthlyRepo.syncFromCurrent())
                .doOnSuccess(v -> log.info("Monthly Sync Complete"));
    }
}
