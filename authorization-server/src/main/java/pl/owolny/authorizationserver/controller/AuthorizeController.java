package pl.owolny.authorizationserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.owolny.authorizationserver.controller.request.IsTokenValidRequest;
import pl.owolny.authorizationserver.controller.response.IsTokenValidResponse;
import pl.owolny.authorizationserver.utils.TokenUtils;

/**
 * Checking if access token from authentication server is valid.
 * If it is valid, then generate new JWT token for gateway server.
 * Gateway server will use this new JWT token to access other services.
 * If access token is not valid, then return 404.
 */

@RestController
@RequestMapping()
public class AuthorizeController {
    
    private final TokenUtils tokenUtils;

    public AuthorizeController(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    @GetMapping("/is-token-valid")
    public ResponseEntity<IsTokenValidResponse> isTokenValid(@RequestBody IsTokenValidRequest isTokenValidRequest) {

        if (!tokenUtils.validateAccessToken(isTokenValidRequest.accessToken())) {
            return ResponseEntity.notFound().build();
        }

        String userId = tokenUtils.getUserIdFromAccessToken(isTokenValidRequest.accessToken());
        String jwtToken = tokenUtils.createJwt(userId);

        return ResponseEntity.ok(new IsTokenValidResponse(jwtToken));
    }
}
