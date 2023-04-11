package pl.owolny.authenticationserver.redis.authtokens;

import org.springframework.stereotype.Service;
import pl.owolny.authenticationserver.redis.RedisService;

@Service
public class AuthTokensService extends RedisService<AuthTokens, String> {

    public AuthTokensService(AuthTokensRepository repository) {
        super(repository);
    }
}
