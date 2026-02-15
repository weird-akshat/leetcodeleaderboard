package org.iecse.leetcodeleaderboard.repo;

import org.iecse.leetcodeleaderboard.entity.DailyUserProfileState;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
public interface DailyUserProfileStateRepo extends ReactiveCrudRepository<DailyUserProfileState,Long> {
    @Query(value = "SELECT c.leetcode_id, " +
            "c.last_updated, " +
            "c.is_active, " +
            "(c.easy - COALESCE(d.easy, 0)) as easy, " +
            "(c.medium - COALESCE(d.medium, 0)) as medium, " +
            "(c.hard - COALESCE(d.hard, 0)) as hard, " +
            "((c.easy - COALESCE(d.easy, 0)) * :m1 + " +
            " (c.medium - COALESCE(d.medium, 0)) * :m2 + " +
            " (c.hard - COALESCE(d.hard, 0)) * :m3) as total_score " +
            "FROM current_user_profile_state c " +
            "INNER JOIN daily_user_profile_state d ON c.leetcode_id = d.leetcode_id " +
             "WHERE c.is_active = true " +
            "ORDER BY total_score DESC")
    Flux<DailyUserProfileState> getDailyGainsLeaderboard(int m1, int m2, int m3);

    @Modifying
    @Query("DELETE FROM daily_user_profile_state")
    Mono<Void> clearTable();

    @Modifying
    @Query("INSERT INTO daily_user_profile_state (leetcode_id, easy, medium, hard) " +
            "SELECT leetcode_id, easy, medium, hard FROM current_user_profile_state")
    Mono<Void> syncFromCurrent();
}
