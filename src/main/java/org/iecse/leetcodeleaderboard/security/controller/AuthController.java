package org.iecse.leetcodeleaderboard.security.controller;
import lombok.RequiredArgsConstructor;
import org.iecse.leetcodeleaderboard.dto.UserData;
import org.iecse.leetcodeleaderboard.security.dto.*;
import org.iecse.leetcodeleaderboard.security.jwt.JwtTokenProvider;
import org.iecse.leetcodeleaderboard.security.service.AppUserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AppUserService userService;


    @PostMapping("/signup")

    public Mono<ResponseEntity<Boolean>> signup(@RequestBody SignupRequest request) {
        return userService.registerUser(request)
                .map(appUser -> new ResponseEntity<>(HttpStatus.OK));
    }
    @PostMapping("/signup/otp")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Boolean>> saveUser(@RequestBody OtpRequest request) {
        return userService.saveUser(request)
                .map(appUser -> new ResponseEntity<>(HttpStatus.OK));
    }

    @PutMapping("/update/password")
    public Mono<ResponseEntity<UserResponse>>updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest){
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(updatePasswordRequest.getUsername(),updatePasswordRequest.getPassword()))
                .flatMap(auth->userService.updatePassword(updatePasswordRequest)).map(
                        appUser -> new UserResponse(
                                appUser.getId(),
                                appUser.getUsername(),
                                appUser.getLeetcodeId(),
                                appUser.getRole()
                        )
                ).map(userResponse -> new ResponseEntity<>(userResponse, HttpStatus.OK));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest request) {
        return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                )
                .flatMap(auth -> userService.findByUsername(request.getUsername()))
                .map(user -> {
                    String token = tokenProvider.createToken(
                            user.getUsername(),
                            user.getRole(),
                            user.getLeetcodeId()

                    );

                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    return ResponseEntity.ok().headers(headers).body(new LoginResponse(token));
                });
    }
}