package com.exe.carenest.authorizeservice.config;

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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        log.info("Authorization header: {}", header);
        if(header !=  null && header.startsWith("X-Key-APT")){
            chain.doFilter(request, response);
        }

        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);
                Claims claims = jwtProvider.claimToken(token);
                String username = claims.getSubject();
                List<?> authorities = (List<?>) claims.get("authorities");
                log.info("Token claims: username={}, authorities={}", username, authorities);

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

}
