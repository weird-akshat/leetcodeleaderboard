package org.iecse.leetcodeleaderboard.security.service;


import lombok.RequiredArgsConstructor;

import org.iecse.leetcodeleaderboard.security.repo.AppUserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final AppUserRepository repository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(appUser -> User.withUsername(appUser.getUsername())
                        .password(appUser.getPassword())
                        .authorities(appUser.getRole())
                        .disabled(!appUser.isActive())
                        .build()
                );
    }
}