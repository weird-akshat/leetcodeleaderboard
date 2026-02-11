package org.iecse.leetcodeleaderboard.repo;


import org.iecse.leetcodeleaderboard.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.Optional;


public interface AppUserRepo extends ReactiveCrudRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

}
