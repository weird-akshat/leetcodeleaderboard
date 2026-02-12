package org.iecse.leetcodeleaderboard.service.security;




import lombok.AllArgsConstructor;
import org.iecse.leetcodeleaderboard.entity.AppUser;
import org.iecse.leetcodeleaderboard.repo.AppUserRepo;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepo appUserRepo;
    @Override


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepo.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not found: "+username));


        return User.builder().username(user.getEmail()).password(user.getPassword()).roles(user.getRole()).build();

    }

}
