package org.iecse.leetcodeleaderboard.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final CacheManager cacheManager;

    public OtpService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void saveOtp(String key, String otp) {
        Cache cache = cacheManager.getCache("otpCache");
        if (cache != null) {
            cache.put(key, otp);
        }
    }

    public String getOtp(String key) {
        Cache cache = cacheManager.getCache("otpCache");
        return (cache != null && cache.get(key) != null) ? cache.get(key, String.class) : null;
    }

    public void clearOtp(String key) {
        Cache cache = cacheManager.getCache("otpCache");
        if (cache != null) {
            cache.evict(key);
        }
    }
}