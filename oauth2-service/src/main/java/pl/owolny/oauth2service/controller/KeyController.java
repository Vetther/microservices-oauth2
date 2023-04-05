package pl.owolny.oauth2service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.owolny.oauth2service.key.Key;
import pl.owolny.oauth2service.key.KeyService;

import java.util.Optional;

@RestController
@RequestMapping("/keys")
public class KeyController {

    private final KeyService keyService;

    public KeyController(KeyService keyService) {
        this.keyService = keyService;
    }

    @GetMapping("/{key}")
    public ResponseEntity<Key> getKey(@PathVariable String key) {
        Optional<Key> optionalKey = keyService.findKey(key);

        if (optionalKey.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(optionalKey.get());
    }
}