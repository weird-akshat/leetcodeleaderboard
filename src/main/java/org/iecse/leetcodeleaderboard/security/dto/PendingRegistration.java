package org.iecse.leetcodeleaderboard.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.iecse.leetcodeleaderboard.security.entity.AppUser;

@Data
@AllArgsConstructor
public class PendingRegistration {
    private AppUser appUser;
    private String otp;
}
