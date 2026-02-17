package org.iecse.leetcodeleaderboard.service;

import org.iecse.leetcodeleaderboard.security.dto.PendingRegistration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final CacheManager cacheManager;

    public OtpService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    public void savePendingRegistration(String email, PendingRegistration data) {
        Cache cache = cacheManager.getCache("otpCache");
        if (cache != null) {
            cache.put(email, data);
        }
    }

    public void saveOTP(String email, String otp){
        Cache cache = cacheManager.getCache(("otpCache"));
        if (cache!=null){
            cache.put(email,otp);
        }
    }
    public String getOTP(String email){
        Cache cache= cacheManager.getCache("otpCache");
        if(cache!=null){
            return cache.get(email,String.class);
        }
        else {
            return null;
        }
    }

    public PendingRegistration getPendingRegistration(String email) {
        Cache cache = cacheManager.getCache("otpCache");
        return (cache != null) ? cache.get(email, PendingRegistration.class) : null;
    }
    public void clearOtp(String key) {
        Cache cache = cacheManager.getCache("otpCache");
        if (cache != null) {
            cache.evict(key);
        }
    }
}