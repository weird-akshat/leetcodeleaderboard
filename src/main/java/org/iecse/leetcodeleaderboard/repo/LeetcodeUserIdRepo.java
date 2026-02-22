package org.iecse.leetcodeleaderboard.repo;

import org.iecse.leetcodeleaderboard.entity.LeetcodeUserId;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface LeetcodeUserIdRepo extends ReactiveCrudRepository<LeetcodeUserId, String> {
    @Modifying
    @Query("INSERT INTO leetcode_user_id (user_id) VALUES (:#{#entity.userId})")
    Mono<Void> insertUser(@Param("entity") LeetcodeUserId entity);
}
