package pl.owolny.authenticationserver.controller.tokens;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.owolny.authenticationserver.controller.tokens.request.GetTokensRequest;
import pl.owolny.authenticationserver.controller.tokens.response.GetTokensResponse;
import pl.owolny.authenticationserver.redis.authtokens.AuthTokens;
import pl.owolny.authenticationserver.redis.authtokens.AuthTokensService;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tokens")
public class TokensController {

    private final AuthTokensService authTokensService;

    public TokensController(AuthTokensService authTokensService) {
        this.authTokensService = authTokensService;
    }

    @GetMapping("/get")
    public ResponseEntity<GetTokensResponse> getTokens(@RequestBody GetTokensRequest getTokensRequest) {

        Optional<AuthTokens> authTokens = authTokensService.find(getTokensRequest.secretKey());

        if (authTokens.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new GetTokensResponse(authTokens.get().accessToken(), authTokens.get().refreshToken()));
    }
}
