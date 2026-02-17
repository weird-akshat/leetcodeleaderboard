package org.iecse.leetcodeleaderboard.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UpdatePasswordRequest {
    String username;
    String password;
    String newPassword;

}
