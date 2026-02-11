package org.iecse.leetcodeleaderboard.repo;

import org.iecse.leetcodeleaderboard.entity.CurrentUserProfileState;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CurrentUserProfileStateRepo extends ReactiveCrudRepository<CurrentUserProfileState,Long> {
    Mono<CurrentUserProfileState> findByLeetcodeId(String leetcodeId);
    @Query("SELECT * FROM current_user_profile_state " + "ORDER BY (easy * :m1 + medium * :m2 + hard * :m3) DESC")
    Flux<CurrentUserProfileState> findTopRanked(double m1, double m2, double m3);
}
