package pl.owolny.oauth2service.redis.provideruserdata;

import org.springframework.stereotype.Service;
import pl.owolny.oauth2service.redis.RedisService;

@Service
public class ProviderUserDataService extends RedisService<ProviderUserData, String> {

    public ProviderUserDataService(ProviderUserDataRepository repository) {
        super(repository);
    }
}
