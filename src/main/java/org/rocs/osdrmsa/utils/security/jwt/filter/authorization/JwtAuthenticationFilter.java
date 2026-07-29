package org.rocs.osdrmsa.utils.security.jwt.filter.authorization;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.rocs.osdrmsa.utils.security.constant.SecurityConstant;
import org.rocs.osdrmsa.utils.security.jwt.provider.token.JwtService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Reads the "Authorization: Bearer <token>" header, validates it via
 * JwtService, and populates the SecurityContext so downstream
 * @PreAuthorize checks can rely on the request being authenticated.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(SecurityConstant.AUTH_HEADER);

        if (header != null && header.startsWith(SecurityConstant.TOKEN_PREFIX)) {

            String token = header.substring(SecurityConstant.TOKEN_PREFIX.length());
            Optional<DecodedJWT> decoded = jwtService.verify(token);

            if (decoded.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {

                String username = jwtService.extractUsername(decoded.get());
                String role = jwtService.extractRole(decoded.get());
                String rawAuthorities = jwtService.extractAuthorities(decoded.get());

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (role != null) {
                    authorities.add(new SimpleGrantedAuthority(role));
                }
                if (rawAuthorities != null && !rawAuthorities.isBlank()) {
                    Arrays.stream(rawAuthorities.split(","))
                            .map(String::trim)
                            .filter(a -> !a.isEmpty())
                            .map(SimpleGrantedAuthority::new)
                            .forEach(authorities::add);
                }

                var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}