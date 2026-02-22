package org.iecse.leetcodeleaderboard.security.service;


import lombok.RequiredArgsConstructor;


import org.iecse.leetcodeleaderboard.entity.LeetcodeUserId;
import org.iecse.leetcodeleaderboard.repo.LeetcodeUserIdRepo;
import org.iecse.leetcodeleaderboard.security.dto.OtpRequest;
import org.iecse.leetcodeleaderboard.security.dto.PendingRegistration;
import org.iecse.leetcodeleaderboard.security.dto.SignupRequest;
import org.iecse.leetcodeleaderboard.security.dto.UpdatePasswordRequest;
import org.iecse.leetcodeleaderboard.security.entity.AppUser;
import org.iecse.leetcodeleaderboard.security.repo.AppUserRepository;
import org.iecse.leetcodeleaderboard.service.LeaderboardService;
import org.iecse.leetcodeleaderboard.service.MailService;
import org.iecse.leetcodeleaderboard.service.OtpService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AppUserService {
    private final LeaderboardService leaderboardService;
    private final AppUserRepository repository;
    private final LeetcodeUserIdRepo leetcodeUserIdRepo;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;
    private final OtpService otpService;
    private final MailService mailService;
    public Mono<AppUser> findByUsername(String username) {
        return repository.findByUsername(username);
    }
    public Mono<AppUser> saveUser(OtpRequest otpRequest){
        PendingRegistration pendingRegistration = otpService.getPendingRegistration(otpRequest.getUsername());
        if (pendingRegistration.getOtp().equals(otpRequest.getOtp())){
            otpService.clearOtp(otpRequest.getUsername());
            return repository.save(pendingRegistration.getAppUser());
        }
        else{
            throw new RuntimeException("OTP Request didn't match");
        }
    }

    public Mono<AppUser> updatePassword(UpdatePasswordRequest updatePasswordRequest){
        return repository.findByUsername(updatePasswordRequest.getUsername()).map(appUser -> {
            appUser.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
            return appUser;
        }).flatMap(repository::save);
    }
    public Mono<AppUser> forgotPassword(String email, String newPassword){

        int otpNum = secureRandom.nextInt(100000);
        String otp = String.format("%06d",otpNum);
        return repository.findByUsername(email).switchIfEmpty(Mono.error(new RuntimeException("No user for this email")) )
                .map(appUser ->{
                    PendingRegistration pendingRegistration = new PendingRegistration(appUser,otp);
                    appUser.setPassword(passwordEncoder.encode(newPassword));
                    otpService.savePendingRegistration(email,pendingRegistration);
                    mailService.sendPlainText(email,"Forgot Password"," OPT: "+ otp);

                    return appUser;

                } );

    }
    public Mono<AppUser> registerUser(SignupRequest request) {

        return repository.findByUsername(request.getUsername())
                .flatMap(existing -> Mono.<AppUser>error(new RuntimeException("User already exists")))
                .switchIfEmpty(Mono.defer(() ->
                     leaderboardService.verifyLeetcodeId(request.getLeetcodeId(), request.getUsername())
                             .flatMap(verified->{
                                 if (verified)
                                    return leetcodeUserIdRepo.insertUser(new LeetcodeUserId(request.getLeetcodeId()));
                                 else
                                     throw new RuntimeException();

                             }).flatMap(
                            verified-> {
                                    AppUser newUser = new AppUser();
                                    newUser.setUsername(request.getUsername());
                                    newUser.setPassword(passwordEncoder.encode(request.getPassword()));
                                    newUser.setLeetcodeId(request.getLeetcodeId());
                                    newUser.setRole("ROLE_USER");
                                    newUser.setActive(true);
                                    int otpNum = secureRandom.nextInt(100000);
                                    String otp = String.format("%06d",otpNum);
                                    PendingRegistration pendingRegistration = new PendingRegistration(newUser,otp);
                                    otpService.savePendingRegistration(request.getUsername(), pendingRegistration);
                                    mailService.sendPlainText(request.getUsername(), "LeetLead OTP","Your OTP is: "+ otp);
                                    return Mono.just(newUser);
                            }
                    )


                ));
    }
}