package org.iecse.leetcodeleaderboard.repo;

import org.iecse.leetcodeleaderboard.entity.UserProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserProfileRepo extends ReactiveCrudRepository<UserProfile,Long> {
    Mono<UserProfile> findByLeetcodeId(String leetcodeId);
}
