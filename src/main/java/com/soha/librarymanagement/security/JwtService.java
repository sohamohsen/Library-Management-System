package com.soha.librarymanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey; // Base64-encoded secret. For HS256, 256-bit (32 bytes) BEFORE Base64.

    @Value("${jwt.expiration}")
    private long jwtExpiration; // in milliseconds

    /** ------- Public API ------- */

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRoleNames(String token) {
        return extractClaim(token, claims -> {
            Object raw = claims.get("roles");
            if (raw instanceof Collection<?> col) {
                return col.stream().map(String::valueOf).toList();
            }
            // Backward compat if you ever stored a single "role" string
            Object single = claims.get("role");
            return single != null ? List.of(String.valueOf(single)) : List.of();
        });
    }

    public List<GrantedAuthority> extractAuthorities(String token) {
        return extractRoleNames(token).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /** Generate token with roles embedded (stateless) */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Put all authorities (e.g. ["ROLE_ADMIN", "PERM_X"])
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.startsWith("ROLE_") ? auth : "ROLE_" + auth)
                .map(String::toUpperCase)                  // <<< normalize case
                .distinct()
                .toList();
        claims.put("roles", roles);

        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Strict validation against subject & expiry. */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null
                && username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    /** ------- Internals ------- */

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = parseClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        Date exp = extractClaim(token, Claims::getExpiration);
        return exp.before(new Date());
    }

    private Claims parseClaims(String token) {
        if (token == null || token.isBlank()) {
            throw new JwtException("Empty token");
        }

        // Allow small clock skew (e.g., reverse proxy time drift)
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .setAllowedClockSkewSeconds(60) // 1 minute skew tolerance
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        // secretKey MUST be Base64 for this to work:
        // e.g. 32 random bytes -> Base64 -> set in application.yml as jwt.secret
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
