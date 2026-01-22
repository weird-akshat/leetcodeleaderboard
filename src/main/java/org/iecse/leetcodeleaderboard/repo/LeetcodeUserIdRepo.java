package org.iecse.leetcodeleaderboard.repo;

import org.iecse.leetcodeleaderboard.entity.LeetcodeUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeetcodeUserIdRepo extends JpaRepository<LeetcodeUserId, String> {
}
