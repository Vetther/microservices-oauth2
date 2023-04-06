package pl.owolny.oauth2service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.owolny.oauth2service.redis.RedisService;
import pl.owolny.oauth2service.redis.provideruserdata.ProviderUserData;
import pl.owolny.oauth2service.redis.provideruserdata.ProviderUserDataService;

import java.util.Optional;

@RestController
@RequestMapping("/keys")
public class KeyController {

    private final ProviderUserDataService providerUserDataService;

    public KeyController(ProviderUserDataService providerUserDataService) {
        this.providerUserDataService = providerUserDataService;
    }

    @GetMapping("/{key}")
    public ResponseEntity<ProviderUserData> getKey(@PathVariable String key) {
        Optional<ProviderUserData> optionalKey = this.providerUserDataService.find(key);

        if (optionalKey.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(optionalKey.get());
    }
}