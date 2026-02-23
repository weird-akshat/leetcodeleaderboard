package org.iecse.leetcodeleaderboard.security.service;

import org.iecse.leetcodeleaderboard.security.entity.AppUser;
import org.iecse.leetcodeleaderboard.security.repo.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReactiveUserDetailsServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Test
    void findByUsernameShouldMapAppUserToUserDetails() {
        AppUser appUser = new AppUser(1L, "mail@example.com", "pass", "lc", "ROLE_USER", true);
        when(appUserRepository.findByUsername("mail@example.com")).thenReturn(Mono.just(appUser));

        ReactiveUserDetailsServiceImpl service = new ReactiveUserDetailsServiceImpl(appUserRepository);

        StepVerifier.create(service.findByUsername("mail@example.com"))
                .assertNext(userDetails -> assertMappedUser(userDetails, appUser))
                .verifyComplete();
    }

    @Test
    void findByUsernameShouldReturnEmptyWhenUserMissing() {
        when(appUserRepository.findByUsername("missing@example.com")).thenReturn(Mono.empty());

        ReactiveUserDetailsServiceImpl service = new ReactiveUserDetailsServiceImpl(appUserRepository);

        StepVerifier.create(service.findByUsername("missing@example.com"))
                .verifyComplete();
    }

    private static void assertMappedUser(UserDetails userDetails, AppUser appUser) {
        assertThat(userDetails.getUsername()).isEqualTo(appUser.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(appUser.getPassword());
        assertThat(userDetails.isEnabled()).isEqualTo(appUser.isActive());
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly(appUser.getRole());
    }
}
