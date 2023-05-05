package pl.owolny.authenticationserver.controller.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.owolny.authenticationserver.authprovider.AuthProvider;
import pl.owolny.authenticationserver.client.SecretKeyClient;
import pl.owolny.authenticationserver.client.UserAdminClient;
import pl.owolny.authenticationserver.client.config.RestResponse;
import pl.owolny.authenticationserver.client.model.UserDTO;
import pl.owolny.authenticationserver.redis.authtokens.AuthTokensService;
import pl.owolny.authenticationserver.utils.OAuth2RoutesUtils;
import pl.owolny.authenticationserver.jwt.JwtService;

import static pl.owolny.authenticationserver.controller.ResponseBodyError.PROVIDER_USER_DATA_NOT_FOUND;

@Slf4j
@RestController
@RequestMapping("/login/oauth2")
public class LoginOAuth2Controller {

    private final AuthTokensService authTokensService;
    private final JwtService jwtService;
    private final UserAdminClient userAdminClient;
    private final SecretKeyClient secretKeyClient;

    public LoginOAuth2Controller(AuthTokensService authTokensService, JwtService jwtService, UserAdminClient userAdminClient, SecretKeyClient secretKeyClient) {
        this.authTokensService = authTokensService;
        this.jwtService = jwtService;
        this.userAdminClient = userAdminClient;
        this.secretKeyClient = secretKeyClient;
    }

    @GetMapping("/{authProvider}")
    public ResponseEntity<Void> loginOAuth2(HttpServletResponse response,
                                               @PathVariable(name = "authProvider") String authProviderString,
                                               @RequestParam(name = "redirect_uri") String frontendRedirectUri) {

        AuthProvider authProvider = AuthProvider.fromString(authProviderString);

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String callbackUrl = baseUrl + "/auth/login/oauth2/callback/" + authProvider;
        String providerUrl = OAuth2RoutesUtils.getOAuth2Route(authProvider, callbackUrl, frontendRedirectUri);

        response.setHeader("Location", providerUrl);
        response.setStatus(HttpStatus.FOUND.value());
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @GetMapping("/callback/{authProvider}")
    public ResponseEntity<Void> loginOAuth2Callback(HttpServletResponse response,
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

        // 2. Searching for user in user-service with this linked account id and provider
        // TODO: dodać obsługę tokenu który trzeba przesłać w headerze, ponieważ teraz jest to publiczny endpoint
        //  i każdy może go wywołać i uzyskać dane użytkownika
        RestResponse<UserDTO> getUserResponse = this.userAdminClient.getUserFromProvider(
                new UserAdminClient.UserFromProviderRequest(authProvider, providerUserData.providerUserId()));
        if (!getUserResponse.success() && getUserResponse.data().isEmpty()) {
            return returnError(response, frontendRedirectUri, getUserResponse.error());
        }

        response.setHeader("Location", frontendRedirectUri + "?user=" + getUserResponse.data().get().name());
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
