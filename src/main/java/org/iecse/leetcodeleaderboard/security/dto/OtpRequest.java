package org.iecse.leetcodeleaderboard.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OtpRequest {
    private String username;
    private String otp;
}
