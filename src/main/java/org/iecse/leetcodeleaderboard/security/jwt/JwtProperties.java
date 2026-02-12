package org.iecse.leetcodeleaderboard.security.jwt;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "jwt")
@Data
@Configuration
public class JwtProperties {
    @Value("${JWT_KEY}")
    private String secretKey ;
    @Value("${EXPIRATION_TIME}")
    private long validityInMs ;
}