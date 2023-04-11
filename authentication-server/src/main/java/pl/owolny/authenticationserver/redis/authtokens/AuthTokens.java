package pl.owolny.authenticationserver.redis.authtokens;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * Auth Tokens are used to store access tokens and refresh tokens under a secret key.
 * The secret key is used to retrieve the tokens from the database.
 * It is made because we don't want to send tokens in the URL, so we need to make one more request
 * in TokensController to get them from Redis.
 */

@RedisHash(timeToLive = 5 * 60)
public record AuthTokens(@Id String secretKey, String accessToken, String refreshToken) {
}