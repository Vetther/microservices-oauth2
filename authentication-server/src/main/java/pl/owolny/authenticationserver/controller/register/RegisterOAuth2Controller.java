package pl.owolny.authenticationserver.controller.register;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.owolny.authenticationserver.authprovider.AuthProvider;
import pl.owolny.authenticationserver.client.RestClient;
import pl.owolny.authenticationserver.controller.register.request.CreateUserRequest;
import pl.owolny.authenticationserver.controller.register.request.LinkedAccountRequest;
import pl.owolny.authenticationserver.controller.register.response.CreateUserResponse;
import pl.owolny.authenticationserver.controller.register.response.ProviderUserDataResponse;
import pl.owolny.authenticationserver.redis.authtokens.AuthTokens;
import pl.owolny.authenticationserver.redis.authtokens.AuthTokensService;
import pl.owolny.authenticationserver.utils.OAuth2RoutesUtils;
import pl.owolny.authenticationserver.utils.TokenUtils;

@RestController
@RequestMapping("/register/oauth2")
@Slf4j
public class RegisterOAuth2Controller {

    private final AuthTokensService authTokensService;
    private final TokenUtils tokenUtils;
    private final RestClient restClient;

    public RegisterOAuth2Controller(AuthTokensService authTokensService, TokenUtils tokenUtils, RestClient restClient) {
        this.authTokensService = authTokensService;
        this.tokenUtils = tokenUtils;
        this.restClient = restClient;
    }

    @GetMapping("/{authProvider}")
    public ResponseEntity<Void> registerOAuth2(HttpServletResponse response,
                               @PathVariable(name = "authProvider") String authProviderString,
                               @RequestParam(name = "redirect_uri") String frontendRedirectUri) {

        AuthProvider authProvider = AuthProvider.fromString(authProviderString);

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String callbackUrl = baseUrl + "/auth/register/oauth2/callback/" + authProvider;
        String providerUrl = OAuth2RoutesUtils.getOAuth2Route(authProvider, callbackUrl, frontendRedirectUri);

        response.setHeader("Location", providerUrl);
        response.setStatus(HttpStatus.FOUND.value());
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @GetMapping("/callback/{authProvider}")
    public ResponseEntity<Void> registerOAuth2Callback(HttpServletResponse response,
                                       HttpServletRequest request,
                                       @RequestParam String key,
                                       @RequestParam(required = false, name = "frontend_uri") String frontendRedirectUri,
                                       @PathVariable(name = "authProvider") String authProviderString) {

        AuthProvider authProvider = AuthProvider.fromString(authProviderString);

        log.info("New callback");
        log.info("Params: " + request.getQueryString());

        // 1. Getting user data from oauth2-service
        ProviderUserDataResponse providerUserData = this.restClient.getProviderUserDataFromKey(key);
        if (providerUserData == null) {
            log.error("Getting provider-user-data: " + "ERROR");
            return returnError(response, frontendRedirectUri, "provider_user_data_not_found");
        }
        log.info("Getting provider-user-data: " + "OK");

        // 2. Creating user in user-service
        CreateUserResponse createUserResponse = this.restClient.createUser(createUserRequest(providerUserData, authProvider));
        if (createUserResponse == null) {
            log.error("Creating user in user-service: " + "ERROR");
            return returnError(response, frontendRedirectUri, "user_creation_error");
        }
        log.info("Creating user in user-service: " + "OK");

        // 3. Creating access token and refresh token and saving them in redis
        String accessToken = tokenUtils.createAccessToken(createUserResponse.name());
        String refreshToken = tokenUtils.createRefreshToken(createUserResponse.name());
        this.authTokensService.save(new AuthTokens(key, accessToken, refreshToken));
        log.info("Creating new redis object with tokens, key: " + key);

        // 4. Redirecting to frontend with key to get tokens
        response.setHeader("Location", frontendRedirectUri + "?secret_key=" + key);
        response.setStatus(HttpStatus.FOUND.value());
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    private ResponseEntity<Void> returnError(HttpServletResponse response, String redirectUri, String error) {
        response.setHeader("Location", redirectUri + "?error=" + error);
        response.setStatus(HttpStatus.FOUND.value());
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    private CreateUserRequest createUserRequest(ProviderUserDataResponse providerUserData, AuthProvider authProvider) {
        return new CreateUserRequest(
                providerUserData.providerUserName(),
                providerUserData.providerUserUsername(),
                null,
                providerUserData.providerUserEmail(),
                providerUserData.providerUserImageUrl(),
                new LinkedAccountRequest(
                        authProvider,
                        providerUserData.providerUserId(),
                        providerUserData.providerUserEmail(),
                        providerUserData.providerUserName()
                )
        );
    }
}
