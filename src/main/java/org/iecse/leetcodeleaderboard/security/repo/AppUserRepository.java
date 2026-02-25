package org.iecse.leetcodeleaderboard.security.repo;




import org.iecse.leetcodeleaderboard.security.entity.AppUser;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AppUserRepository extends R2dbcRepository<AppUser, Long> {
    Mono<AppUser> findByUsername(String username);
    Mono<AppUser> findByLeetcodeId(String leetcodeId);

}