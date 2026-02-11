package org.iecse.leetcodeleaderboard.dto.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@RequiredArgsConstructor

public class SignupRequest {

    private  String email;
    private String password;
    private String fullName;


}
