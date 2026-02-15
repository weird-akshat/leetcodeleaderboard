package org.iecse.leetcodeleaderboard.security.controller;
import lombok.RequiredArgsConstructor;
import org.iecse.leetcodeleaderboard.security.dto.LoginRequest;
import org.iecse.leetcodeleaderboard.security.dto.LoginResponse;
import org.iecse.leetcodeleaderboard.security.dto.SignupRequest;
import org.iecse.leetcodeleaderboard.security.dto.UserResponse;
import org.iecse.leetcodeleaderboard.security.jwt.JwtTokenProvider;
import org.iecse.leetcodeleaderboard.security.service.AppUserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AppUserService userService;


    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponse> signup(@RequestBody SignupRequest request) {
        return userService.registerUser(request)
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getLeetcodeId(),
                        user.getRole()
                ));
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