package org.iecse.leetcodeleaderboard.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iecse.leetcodeleaderboard.dto.security.JwtRequest;
import org.iecse.leetcodeleaderboard.dto.security.JwtResponse;
import org.iecse.leetcodeleaderboard.dto.security.SignupRequest;
import org.iecse.leetcodeleaderboard.security.JwtHelper;
import org.iecse.leetcodeleaderboard.service.security.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final JwtHelper helper;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        authService.doAuthenticate(request.getEmail(), request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);
        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> signup(@RequestBody SignupRequest signupRequest){
        authService.signup(signupRequest.getFullName(), signupRequest.getEmail(),signupRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(signupRequest.getEmail());
        String token= this.helper.generateToken(userDetails);
        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> exception(){
        return new ResponseEntity<>("Bad Request !!", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> exceptionHandler() {
        return new ResponseEntity<>("Credentials Invalid !!", HttpStatus.UNAUTHORIZED);
    }
}

