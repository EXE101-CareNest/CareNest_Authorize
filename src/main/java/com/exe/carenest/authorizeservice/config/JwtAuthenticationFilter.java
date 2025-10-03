package com.exe.carenest.authorizeservice.config;

import com.exe.carenest.authorizeservice.service.impl.RedisService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        log.info("Authorization header: {}", header);
        if(header !=  null && header.startsWith("X-Key-APT")){
            chain.doFilter(request, response);
            return;
        }
//
//        if(request.getRequestURL())

        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);

                if (isTokenBlacklisted(token)) {
                    log.warn("Token is blacklisted: {}", token.substring(0, 10) + "...");
                    // Don't set authentication - let it fail with 401
                    chain.doFilter(request, response);
                    return;
                }

                Claims claims = jwtProvider.claimToken(token);
                String username = claims.getSubject(); // "admin"
                String role = (String) claims.get("role"); // "ROLE_ADMIN"

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(role));

                log.info("Token claims: username={}, role={}, authorities={}", username, role, authorities);


                Collection<? extends GrantedAuthority> grantedAuthorities = authorities.stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.toString()))
                        .collect(Collectors.toList());
                log.info("Granted authorities: {}", grantedAuthorities);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Set authentication for user: {}", username);
            } catch (Exception ex) {
                log.error("JWT processing failed: {}", ex.getMessage(), ex);
                // KHÔNG set authentication nếu lỗi (để Spring vẫn 401 đúng)
            }
        } else {
            log.warn("No valid Bearer token found");
        }
        chain.doFilter(request, response);
    }

    private boolean isTokenBlacklisted(String token) {
        try {
            String blacklistKey = "blacklist:" + token;
            Object blacklistValue = redisService.get(blacklistKey);

            boolean isBlacklisted = blacklistValue != null;

            if (isBlacklisted) {
                log.info("Token found in blacklist: {}", blacklistKey);
            }

            return isBlacklisted;

        } catch (Exception e) {
            log.error("Error checking blacklist for token: {}", e.getMessage());
            // In case of Redis error, allow the token to proceed
            // Better to have temporary access than complete system failure
            return false;
        }
    }

}
