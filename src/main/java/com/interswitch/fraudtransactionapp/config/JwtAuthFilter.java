package com.interswitch.fraudtransactionapp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;



    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("JWT Filter processing request: " + request.getMethod() + " " + request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        final String userId;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            System.out.println("No Authorization header or not Bearer token, proceeding without authentication");
            filterChain.doFilter(request, response);
            return;

        }


        System.out.println("Processing JWT token...");

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            System.out.println("JWT token validation failed");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String json = """
                
                {
                    "status": 401,
                    "error": "Missing or invalid Authorization token",
                    "timestamp": "%s"
                }
                """.formatted(Instant.now().toString());

            response.getWriter().write(json);            return;
        }
        userId = jwtUtil.extractUsername(token);

        List<String> permissions = jwtUtil.extractPermissions(token);

        var authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();


        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userId, null, authorities
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("JWT token validated and authentication set for user: " + userId);
        }
        filterChain.doFilter(request, response);
    }
}