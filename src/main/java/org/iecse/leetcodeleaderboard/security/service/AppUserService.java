package org.iecse.leetcodeleaderboard.security.service;


import lombok.RequiredArgsConstructor;

import org.iecse.leetcodeleaderboard.security.dto.SignupRequest;
import org.iecse.leetcodeleaderboard.security.entity.AppUser;
import org.iecse.leetcodeleaderboard.security.repo.AppUserRepository;
import org.iecse.leetcodeleaderboard.service.LeaderboardService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
@RequiredArgsConstructor
public class AppUserService {
    private final LeaderboardService leaderboardService;
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    public Mono<AppUser> findByUsername(String username) {
        return repository.findByUsername(username);
    }


    public Mono<AppUser> registerUser(SignupRequest request) {

        return repository.findByUsername(request.getUsername())
                .flatMap(existing -> Mono.<AppUser>error(new RuntimeException("User already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    return leaderboardService.verifyLeetcodeId(request.getLeetcodeId(), request.getUsername()).flatMap(
                            verified-> {
                                if (verified){
                                    AppUser newUser = new AppUser();
                                    newUser.setUsername(request.getUsername());
                                    newUser.setPassword(passwordEncoder.encode(request.getPassword()));
                                    newUser.setLeetcodeId(request.getLeetcodeId());
                                    newUser.setRole("ROLE_USER");
                                    newUser.setActive(true);
                                    return repository.save(newUser);
                                }
                                else{
                                    throw new RuntimeException("LeetcodeId not verified");
                                }
                            }
                    );


                }));
    }
}