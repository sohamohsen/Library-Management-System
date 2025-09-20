package com.soha.librarymanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soha.librarymanagement.errorhandling.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // fallback if token doesn't contain roles
    private final ObjectMapper objectMapper;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final List<String> PUBLIC_PATTERNS = List.of(
            "/auth/**",
            "/actuator/health",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();
        String authHeader = request.getHeader("Authorization");

        // 📝 DEBUG LOGGING
        log.debug("Incoming request: PATH={} | AUTH_HEADER={}", path, authHeader);

        // 1) Allow public endpoints
        if (isPublic(path)) {
            log.debug("Skipping authentication for public endpoint: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Require Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No Bearer token provided or header malformed for path={}", path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7).trim();
            log.debug("Extracted JWT token: {}", token);

            // If already authenticated, just continue
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.debug("SecurityContext already contains authentication. Skipping token parsing.");
                filterChain.doFilter(request, response);
                return;
            }

            // 3) Extract username
            String username = jwtService.extractUsername(token);
            log.debug("Extracted username from token: {}", username);

            if (username == null || username.isBlank()) {
                log.error("Token missing subject (username). Rejecting.");
                writeErrorResponse(response, "Invalid JWT token: missing subject", 401);
                return;
            }

            // 4) Try to get authorities from token directly
            var authorities = jwtService.extractAuthorities(token);
            log.debug("Extracted authorities from token: {}", authorities);

            if (!authorities.isEmpty()) {
                var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("JWT authentication successful for user={} using token claims.", username);
                filterChain.doFilter(request, response);
                return;
            }

            // 5) Fallback: load from DB
            var userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtService.validateToken(token, userDetails)) {
                log.error("JWT validation failed for user={} (token may be expired or tampered).", username);
                writeErrorResponse(response, "Invalid or expired JWT token", 401);
                return;
            }

            var auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info("JWT authentication successful for user={} using database fallback.", username);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            log.warn("JWT token expired for path={} | message={}", path, ex.getMessage());
            writeErrorResponse(response, "JWT token has expired. Please login again.", 401);

        } catch (JwtException ex) {
            log.error("Invalid JWT for path={} | message={}", path, ex.getMessage());
            writeErrorResponse(response, "Invalid JWT token: " + ex.getMessage(), 401);

        } catch (Exception ex) {
            log.error("Unexpected authentication error for path={} | message={}", path, ex.getMessage(), ex);
            writeErrorResponse(response, "Authentication error occurred: " + ex.getMessage(), 401);
        }
    }

    private boolean isPublic(String path) {
        for (String pattern : PUBLIC_PATTERNS) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private void writeErrorResponse(HttpServletResponse response, String message, int statusCode)
            throws IOException {
        ApiResponse<Void> errorResponse = ApiResponse.error(message, statusCode);
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
        response.getWriter().flush();

        log.debug("Error response sent: {}", json);
    }
}
