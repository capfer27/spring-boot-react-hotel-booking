package com.capfer.hotel.booking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
@Slf4j
public class JwtUtilsService {

    private SecretKey secretKey;

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    @Value("${jwt.expiration:6}")
    private long expirationTimeMillis;

    @PostConstruct
    public void init() {
        // Initialize the secret key from the provided string
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        this.secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        log.info("JWT Secret Key initialized successfully.");
    }

    public String generateToken(String email) {
        // Use a JWT library like JJWT to create the token
         return Jwts.builder()
                 .subject(email)
                 .issuedAt(new Date(System.currentTimeMillis()))
                 .expiration(new Date(System.currentTimeMillis() + getEffectiveExpirationTime(6)))
                 .signWith(secretKey)
                 .compact();
    }

    // Validate the token and extract the email (subject)
    public String getEmailFromToken(String token) {
       return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, java.util.function.Function<Claims, T> claimsResolver) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token");
        }
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        String emailFromToken = getEmailFromToken(token);
        return emailFromToken.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    private long getEffectiveExpirationTime(long months) {
        // This handles leap years and months with 28, 30, or 31 days perfectly.
        // ZonedDateTime.now(ZoneOffset.UTC)
        return ZonedDateTime.now()
                .plusMonths(months)
                .toInstant()
                .toEpochMilli();
    }
}
