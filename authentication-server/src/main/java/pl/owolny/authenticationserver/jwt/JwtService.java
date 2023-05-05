package pl.owolny.authenticationserver.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import pl.owolny.authenticationserver.utils.KeyUtils;

import java.io.IOException;
import java.security.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Configuration
@Slf4j
public class JwtService {

    @Value("${app.jwt.accessTokenExpirationMinutes}")
    private Long accessTokenExpirationMinutes;

    @Value("${app.jwt.refreshTokenExpirationMinutes}")
    private Long refreshTokenExpirationMinutes;

    @Getter private final PrivateKey privateKey;
    @Getter private final PublicKey publicKey;

    public JwtService() throws IOException {
        this.privateKey = KeyUtils.readPrivateKey("classpath:certs/private.pem");
        this.publicKey = KeyUtils.readPublicKey("classpath:certs/public.pem");
    }

    private String createToken(String username, Long tokenExpirationMinutes) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(tokenExpirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(this.privateKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(this.publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(this.publicKey)
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

    public String createAccessToken(String username) {
        return createToken(username, accessTokenExpirationMinutes);
    }

    public String createRefreshToken(String username) {
        return createToken(username, refreshTokenExpirationMinutes);
    }

    public String createAccessTokenFromRefreshToken(String refreshToken) {
        String username = getUsernameFromToken(refreshToken);
        return createToken(username, accessTokenExpirationMinutes);
    }
}