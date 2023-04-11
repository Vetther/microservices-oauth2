package pl.owolny.authenticationserver.redis.authtokens;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthTokensRepository extends CrudRepository<AuthTokens, String> {
}