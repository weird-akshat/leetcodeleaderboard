//package org.iecse.leetcodeleaderboard.security.service;
//
//import org.iecse.leetcodeleaderboard.entity.LeetcodeUserId;
//import org.iecse.leetcodeleaderboard.repo.CurrentUserProfileStateRepo;
//import org.iecse.leetcodeleaderboard.repo.LeetcodeUserIdRepo;
//import org.iecse.leetcodeleaderboard.security.dto.OtpRequest;
//import org.iecse.leetcodeleaderboard.security.dto.PendingRegistration;
//import org.iecse.leetcodeleaderboard.security.dto.SignupRequest;
//import org.iecse.leetcodeleaderboard.security.dto.UpdatePasswordRequest;
//import org.iecse.leetcodeleaderboard.security.entity.AppUser;
//import org.iecse.leetcodeleaderboard.security.exception.InvalidOTPException;
//import org.iecse.leetcodeleaderboard.security.exception.UserAlreadyExistsException;
//import org.iecse.leetcodeleaderboard.security.exception.UserNotFoundException;
//import org.iecse.leetcodeleaderboard.security.repo.AppUserRepository;
//import org.iecse.leetcodeleaderboard.service.LeaderboardService;
//import org.iecse.leetcodeleaderboard.service.MailService;
//import org.iecse.leetcodeleaderboard.service.OtpService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.security.SecureRandom;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class AppUserServiceTest {
//
//    @Mock
//    private LeaderboardService leaderboardService;
//
//    @Mock
//    private AppUserRepository repository;
//
//    @Mock
//    private LeetcodeUserIdRepo leetcodeUserIdRepo;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private SecureRandom secureRandom;
//
//    @Mock
//    private OtpService otpService;
//
//    @Mock
//    private MailService mailService;
//
//    @Mock
//    private CurrentUserProfileStateRepo currentUserProfileStateRepo;
//
//    private AppUserService service;
//
//    @BeforeEach
//    void setUp() {
//        service = new AppUserService(
//                leaderboardService,
//                repository,
//                leetcodeUserIdRepo,
//                passwordEncoder,
//                secureRandom,
//                otpService,
//                mailService,
//                currentUserProfileStateRepo
//        );
//    }
//
//    @Test
//    void findByUsernameShouldDelegateToRepository() {
//        AppUser user = appUser("u@x.com", "pass", "lc-id");
//        when(repository.findByUsername("u@x.com")).thenReturn(Mono.just(user));
//
//        StepVerifier.create(service.findByUsername("u@x.com"))
//                .expectNext(user)
//                .verifyComplete();
//    }
//
//    @Test
//    void saveUserShouldPersistWhenOtpMatches() {
//        AppUser user = appUser("u@x.com", "pass", "lc-id");
//        PendingRegistration pendingRegistration = new PendingRegistration(user, "123456");
//        OtpRequest otpRequest = OtpRequest.builder().username("u@x.com").otp("123456").build();
//
//        when(otpService.getPendingRegistration("u@x.com")).thenReturn(pendingRegistration);
//        when(repository.save(user)).thenReturn(Mono.just(user));
//
//        StepVerifier.create(service.saveUser(otpRequest))
//                .expectNext(user)
//                .verifyComplete();
//
//        verify(otpService).clearOtp("u@x.com");
//    }
//
//    @Test
//    void saveUserShouldErrorWhenOtpDoesNotMatch() {
//        AppUser user = appUser("u@x.com", "pass", "lc-id");
//        PendingRegistration pendingRegistration = new PendingRegistration(user, "654321");
//        OtpRequest otpRequest = OtpRequest.builder().username("u@x.com").otp("123456").build();
//
//        when(otpService.getPendingRegistration("u@x.com")).thenReturn(pendingRegistration);
//
//        StepVerifier.create(service.saveUser(otpRequest))
//                .expectError(InvalidOTPException.class)
//                .verify();
//    }
//
//    @Test
//    void updatePasswordShouldEncodeAndPersist() {
//        AppUser user = appUser("u@x.com", "old-pass", "lc-id");
//        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
//                .username("u@x.com")
//                .password("old-pass")
//                .newPassword("new-pass")
//                .build();
//
//        when(repository.findByUsername("u@x.com")).thenReturn(Mono.just(user));
//        when(passwordEncoder.encode("new-pass")).thenReturn("encoded-pass");
//        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
//
//        StepVerifier.create(service.updatePassword(request))
//                .assertNext(updated -> assertThat(updated.getPassword()).isEqualTo("encoded-pass"))
//                .verifyComplete();
//    }
//
//    @Test
//    void updatePasswordShouldErrorWhenUserMissing() {
//        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
//                .username("missing@x.com")
//                .newPassword("new-pass")
//                .build();
//
//        when(repository.findByUsername("missing@x.com")).thenReturn(Mono.empty());
//
//        StepVerifier.create(service.updatePassword(request))
//                .expectError(UserNotFoundException.class)
//                .verify();
//    }
//
//    @Test
//    void forgotPasswordShouldStorePendingAndSendOtpMail() {
//        AppUser user = appUser("u@x.com", "old-pass", "lc-id");
//        when(secureRandom.nextInt(100000)).thenReturn(54321);
//        when(repository.findByUsername("u@x.com")).thenReturn(Mono.just(user));
//        when(passwordEncoder.encode("new-pass")).thenReturn("encoded-new-pass");
//
//        StepVerifier.create(service.forgotPassword("u@x.com", "new-pass"))
//                .assertNext(updated -> assertThat(updated.getPassword()).isEqualTo("encoded-new-pass"))
//                .verifyComplete();
//
//        verify(otpService).savePendingRegistration(eq("u@x.com"), any(PendingRegistration.class));
//        verify(mailService).sendPlainText("u@x.com", "Forgot Password", " OPT: 054321");
//    }
//
//    @Test
//    void forgotPasswordShouldErrorWhenUserMissing() {
//        when(repository.findByUsername("missing@x.com")).thenReturn(Mono.empty());
//
//        StepVerifier.create(service.forgotPassword("missing@x.com", "new-pass"))
//                .expectError(UserNotFoundException.class)
//                .verify();
//    }
//
//    @Test
//    void registerUserShouldErrorWhenUserAlreadyExists() {
//        SignupRequest request = new SignupRequest();
//        request.setUsername("u@x.com");
//        request.setPassword("pass");
//        request.setLeetcodeId("lc-id");
//
//        when(repository.findByUsername("u@x.com")).thenReturn(Mono.just(appUser("u@x.com", "pass", "lc-id")));
//
//        StepVerifier.create(service.registerUser(request))
//                .expectError(UserAlreadyExistsException.class)
//                .verify();
//    }
//
//    @Test
//    @SuppressWarnings({"unchecked", "rawtypes"})
//    void registerUserShouldCreatePendingUserWhenValid() {
//        SignupRequest request = new SignupRequest();
//        request.setUsername("u@x.com");
//        request.setPassword("pass");
//        request.setLeetcodeId("lc-id");
//
//        AppUser expectedSavedUser = new AppUser();
//        expectedSavedUser.setUsername("u@x.com");
//        expectedSavedUser.setPassword("encoded-pass");
//        expectedSavedUser.setLeetcodeId("lc-id");
//        expectedSavedUser.setRole("ROLE_USER");
//        expectedSavedUser.setActive(true);
//
//        when(repository.findByUsername("u@x.com")).thenReturn(Mono.empty());
//        when(leaderboardService.verifyLeetcodeId("lc-id", "u@x.com")).thenReturn(Mono.just(true));
//        when(passwordEncoder.encode("pass")).thenReturn("encoded-pass");
//        when(secureRandom.nextInt(100000)).thenReturn(12345);
//
//        when(leetcodeUserIdRepo.insertUser(any(LeetcodeUserId.class))).thenReturn(Mono.just(new LeetcodeUserId("lc-id")).then());
//        when(repository.save(any(AppUser.class))).thenReturn(Mono.just(expectedSavedUser));
//
//        when(leaderboardService.getIdData(anyString()))
//                .thenReturn(Mono.just(Mockito.mock(org.iecse.leetcodeleaderboard.dto.UserData.class)));
//        when(currentUserProfileStateRepo.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
//        StepVerifier.create(service.registerUser(request))
//                .assertNext(appUser -> {
//                    assertThat(appUser.getUsername()).isEqualTo("u@x.com");
//                    assertThat(appUser.getLeetcodeId()).isEqualTo("lc-id");
//                })
//                .verifyComplete();
//
//        verify(otpService).savePendingRegistration(eq("u@x.com"), any(PendingRegistration.class));
//        verify(mailService).sendPlainText(eq("u@x.com"), anyString(), anyString());
//    }
//
//    private static AppUser appUser(String username, String password, String leetcodeId) {
//        AppUser appUser = new AppUser();
//        appUser.setUsername(username);
//        appUser.setPassword(password);
//        appUser.setLeetcodeId(leetcodeId);
//        appUser.setRole("ROLE_USER");
//        appUser.setActive(true);
//        return appUser;
//    }
//}