package org.iecse.leetcodeleaderboard.security;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@AllArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("JwtAuthenticationFilter invoked for URI: {}", request.getRequestURI());

        // Skip filter for login endpoint
        if (request.getServletPath().equals("/auth/login")) {
            log.info("Skipping JWT filter for login endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        String requestHeader = request.getHeader("Authorization");
        log.info("Authorization header: {}", requestHeader);

        String username = null;
        String token = null;

        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7);
            log.info("Extracted token: {}", token);

            try {
                username = jwtHelper.getUsernameFromToken(token);
                log.info("Username extracted from token: {}", username);
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT token: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                log.warn("JWT token is expired: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                log.error("JWT token is malformed: {}", e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error while parsing JWT: {}", e.getMessage());
            }
        } else {
            log.info("Authorization header is missing or does not start with Bearer");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Validating token for user: {}", username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            boolean isValid = jwtHelper.validateToken(token, userDetails);
            log.info("Token validation result: {}", isValid);

            if (isValid) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("Authentication set in SecurityContextHolder for user: {}", username);
            } else {
                log.warn("JWT validation failed for user: {}", username);
            }
        } else {
            if (username == null) {
                log.info("No username could be extracted from token");
            } else {
                log.info("Authentication already exists in SecurityContextHolder");
            }
        }

        filterChain.doFilter(request, response);
        log.info("Filter chain completed for URI: {}", request.getRequestURI());
    }
}

