package pl.owolny.userservice.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.owolny.userservice.user.UserPrincipal;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Configuration
@Slf4j
public class TokenUtils {

    @Value("${app.auth.tokenSecret}")
    private String tokenSecret;

    public Long getUserIdFromToken(String token) {

        Key key = Keys.hmacShaKeyFor(tokenSecret.getBytes());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {

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
}