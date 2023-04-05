package pl.owolny.oauth2service.key;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class KeyService {

    private final RedisTemplate<String, Key> redisTemplate;

    public KeyService(RedisTemplate<String, Key> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<Key> findKey(String secretKey) {
        Key key = redisTemplate.opsForValue().get(secretKey);
        if (key == null) {
            return Optional.empty();
        }
        return Optional.of(key);
    }

    public void saveKey(Key key) {
        redisTemplate.opsForValue().set(key.getKey(), key);
        redisTemplate.expire(key.getKey(), 5, TimeUnit.MINUTES);
        System.out.println("Utworzono klucz");
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }
}