package pl.owolny.oauth2service.redis.provideruserdata;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderUserDataRepository extends CrudRepository<ProviderUserData, String> {
}