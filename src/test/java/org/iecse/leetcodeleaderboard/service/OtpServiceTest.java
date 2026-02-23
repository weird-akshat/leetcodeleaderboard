package org.iecse.leetcodeleaderboard.service;

import org.iecse.leetcodeleaderboard.security.dto.PendingRegistration;
import org.iecse.leetcodeleaderboard.security.entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService(cacheManager);
    }

    @Test
    void saveOtpShouldPutValueWhenCacheExists() {
        when(cacheManager.getCache("otpCache")).thenReturn(cache);

        otpService.saveOTP("mail@x.com", "123456");

        verify(cache).put("mail@x.com", "123456");
    }

    @Test
    void getOtpShouldReturnNullWhenCacheMissing() {
        when(cacheManager.getCache("otpCache")).thenReturn(null);

        String otp = otpService.getOTP("mail@x.com");

        assertThat(otp).isNull();
    }

    @Test
    void getOtpShouldReadFromCacheWhenPresent() {
        when(cacheManager.getCache("otpCache")).thenReturn(cache);
        when(cache.get("mail@x.com", String.class)).thenReturn("654321");

        String otp = otpService.getOTP("mail@x.com");

        assertThat(otp).isEqualTo("654321");
    }

    @Test
    void savePendingRegistrationShouldPutValueWhenCacheExists() {
        PendingRegistration pendingRegistration = new PendingRegistration(new AppUser(), "111111");
        when(cacheManager.getCache("otpCache")).thenReturn(cache);

        otpService.savePendingRegistration("user@x.com", pendingRegistration);

        verify(cache).put("user@x.com", pendingRegistration);
    }

    @Test
    void getPendingRegistrationShouldReturnNullWhenCacheMissing() {
        when(cacheManager.getCache("otpCache")).thenReturn(null);

        PendingRegistration pendingRegistration = otpService.getPendingRegistration("x@y.com");

        assertThat(pendingRegistration).isNull();
    }

    @Test
    void clearOtpShouldEvictWhenCacheExists() {
        when(cacheManager.getCache("otpCache")).thenReturn(cache);

        otpService.clearOtp("user@x.com");

        verify(cache).evict("user@x.com");
    }

    @Test
    void clearOtpShouldDoNothingWhenCacheMissing() {
        when(cacheManager.getCache("otpCache")).thenReturn(null);

        otpService.clearOtp("user@x.com");

        verify(cache, never()).evict("user@x.com");
    }
}
