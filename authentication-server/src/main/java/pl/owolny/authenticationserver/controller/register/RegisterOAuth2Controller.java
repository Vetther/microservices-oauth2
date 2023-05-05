package pl.owolny.authenticationserver.controller.register;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.owolny.authenticationserver.authprovider.AuthProvider;
import pl.owolny.authenticationserver.client.SecretKeyClient;
import pl.owolny.authenticationserver.client.UserClient;
import pl.owolny.authenticationserver.client.config.RestResponse;
import pl.owolny.authenticationserver.client.model.UserDTO;
import pl.owolny.authenticationserver.redis.authtokens.AuthTokens;
import pl.owolny.authenticationserver.redis.authtokens.AuthTokensService;
import pl.owolny.authenticationserver.utils.OAuth2RoutesUtils;
import pl.owolny.authenticationserver.jwt.JwtService;

import static pl.owolny.authenticationserver.client.UserClient.createUserRequest;
import static pl.owolny.authenticationserver.controller.ResponseBodyError.*;

@RestController
@RequestMapping("/register/oauth2")
@Slf4j
public class RegisterOAuth2Controller {

    private final AuthTokensService authTokensService;
    private final JwtService jwtService;
    private final SecretKeyClient secretKeyClient;
    private final UserClient userClient;

    public RegisterOAuth2Controller(AuthTokensService authTokensService, JwtService jwtService, SecretKeyClient secretKeyClient, UserClient userClient) {
        this.authTokensService = authTokensService;
        this.jwtService = jwtService;
        this.secretKeyClient = secretKeyClient;
        this.userClient = userClient;
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
        RestResponse<SecretKeyClient.ProviderUserDataResponse> providerUserDataResponse = this.secretKeyClient.getProviderUserDataFromSecretKey(key);
        if (!providerUserDataResponse.success() || providerUserDataResponse.data().isEmpty()) {
            return returnError(response, frontendRedirectUri, PROVIDER_USER_DATA_NOT_FOUND.name());
        }
        SecretKeyClient.ProviderUserDataResponse providerUserData = providerUserDataResponse.data().get();

        // 2. Creating user in user-service
        RestResponse<UserDTO> restResponse = this.userClient.createUser(createUserRequest(providerUserData, authProvider));
        if (!restResponse.success() && restResponse.data().isEmpty()) {
            return returnError(response, frontendRedirectUri, restResponse.error());
        }
        UserDTO userDto = restResponse.data().get();

        // 3. Creating access token and refresh token and saving them in redis
        try {
            String accessToken = jwtService.createAccessToken(userDto.name());
            String refreshToken = jwtService.createRefreshToken(userDto.name());
            this.authTokensService.save(new AuthTokens(key, accessToken, refreshToken));
        } catch (Exception e) {
            e.printStackTrace();
            return returnError(response, frontendRedirectUri, TOKEN_CREATION_FAILED.name());
        }

        // 4. Redirecting to frontend with key to get tokens
        response.setHeader("Location", frontendRedirectUri + "?secret_key=" + key);
        response.setStatus(HttpStatus.FOUND.value());
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    private ResponseEntity<Void> returnError(HttpServletResponse response, String redirectUri, String error) {
        log.error("Redirecting to frontend with error: " + error);
        response.setHeader("Location", redirectUri + "?error=" + error);
        response.setStatus(HttpStatus.FOUND.value());
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }
}
