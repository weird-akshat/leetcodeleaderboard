package org.iecse.leetcodeleaderboard.repo;

import org.iecse.leetcodeleaderboard.entity.WeeklyUserProfileState;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
public interface WeeklyUserProfileStateRepo extends ReactiveCrudRepository<WeeklyUserProfileState,Long> {
    @Query("SELECT c.leetcode_id, " +
            "c.last_updated, " +
            "c.is_active, " +
            "(c.easy - w.easy) as easy, " +
            "(c.medium - w.medium) as medium, " +
            "(c.hard - w.hard) as hard, " +
            "((c.easy - w.easy) * :m1 + " +
            " (c.medium - w.medium) * :m2 + " +
            " (c.hard - w.hard) * :m3) as total_score " +
            "FROM current_user_profile_state c " +
            "INNER JOIN weekly_user_profile_state w ON c.leetcode_id = w.leetcode_id " +
            "WHERE c.is_active = true " +
            "ORDER BY total_score DESC")
    Flux<WeeklyUserProfileState> getWeeklyGainsLeaderboard(int m1, int m2, int m3);

    @Modifying
    @Query("DELETE FROM weekly_user_profile_state")
    Mono<Void> clearTable();

    @Modifying
    @Query("INSERT INTO weekly_user_profile_state (leetcode_id, easy, medium, hard) " +
            "SELECT leetcode_id, easy, medium, hard FROM current_user_profile_state")
    Mono<Void> syncFromCurrent();
}
