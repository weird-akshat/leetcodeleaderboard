package org.iecse.leetcodeleaderboard.security.dto;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    String username;
    String password;
}
