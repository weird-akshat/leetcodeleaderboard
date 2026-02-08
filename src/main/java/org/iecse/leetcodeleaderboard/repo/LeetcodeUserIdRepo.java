package org.iecse.leetcodeleaderboard.repo;

import org.iecse.leetcodeleaderboard.entity.LeetcodeUserId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeetcodeUserIdRepo extends ReactiveCrudRepository<LeetcodeUserId, String> {
}
