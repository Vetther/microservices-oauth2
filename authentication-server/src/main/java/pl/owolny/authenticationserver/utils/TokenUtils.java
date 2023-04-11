package pl.owolny.authenticationserver.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Configuration
@Slf4j
public class TokenUtils {

    @Value("${app.auth.accessTokenSecret}")
    private String accessTokenSecret;

    @Value("${app.auth.accessTokenExpirationMinutes}")
    private Long accessTokenExpirationMinutes;

    @Value("${app.auth.refreshTokenSecret}")
    private String refreshTokenSecret;

    @Value("${app.auth.refreshTokenExpirationMinutes}")
    private Long refreshTokenExpirationMinutes;

    private String createToken(String userId, Long tokenExpirationMinutes, String tokenSecret) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(tokenExpirationMinutes, ChronoUnit.MINUTES);
        Key key = Keys.hmacShaKeyFor(tokenSecret.getBytes());

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(key)
                .compact();
    }

    private String getUserIdFromToken(String token, String tokenSecret) {

        Key key = Keys.hmacShaKeyFor(tokenSecret.getBytes());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    private boolean validateToken(String authToken, String tokenSecret) {

        Key key = Keys.hmacShaKeyFor(tokenSecret.getBytes());

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        } catch (ExpiredJwtException ex) {
            log.error("JWT token is expired");
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return false;
    }

    public String createAccessToken(String userId) {
        return createToken(userId, accessTokenExpirationMinutes, accessTokenSecret);
    }

    public String createAccessTokenFromRefreshToken(String refreshToken) {
        String userId = getUserIdFromRefreshToken(refreshToken);
        return createToken(userId, accessTokenExpirationMinutes, accessTokenSecret);
    }

    public String createRefreshToken(String userId) {
        return createToken(userId, refreshTokenExpirationMinutes, refreshTokenSecret);
    }

    public String getUserIdFromAccessToken(String token) {
        return getUserIdFromToken(token, accessTokenSecret);
    }

    public String getUserIdFromRefreshToken(String token) {
        return getUserIdFromToken(token, refreshTokenSecret);
    }

    public boolean validateAccessToken(String authToken) {
        return validateToken(authToken, accessTokenSecret);
    }

    public boolean validateRefreshToken(String authToken) {
        return validateToken(authToken, refreshTokenSecret);
    }
}