package pl.owolny.oauth2service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import pl.owolny.oauth2service.key.Key;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Key> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Key> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Key.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}