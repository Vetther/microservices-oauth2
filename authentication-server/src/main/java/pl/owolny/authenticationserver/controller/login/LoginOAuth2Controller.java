package pl.owolny.authenticationserver.controller.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.owolny.authenticationserver.authprovider.AuthProvider;
import pl.owolny.authenticationserver.client.RestClient;
import pl.owolny.authenticationserver.controller.login.request.GetUserFromProviderRequest;
import pl.owolny.authenticationserver.controller.register.request.CreateUserRequest;
import pl.owolny.authenticationserver.controller.register.request.LinkedAccountRequest;
import pl.owolny.authenticationserver.controller.register.response.CreateUserResponse;
import pl.owolny.authenticationserver.controller.register.response.ProviderUserDataResponse;
import pl.owolny.authenticationserver.redis.authtokens.AuthTokensService;
import pl.owolny.authenticationserver.utils.OAuth2RoutesUtils;
import pl.owolny.authenticationserver.utils.TokenUtils;

@Slf4j
@RestController
@RequestMapping("/login/oauth2")
public class LoginOAuth2Controller {

    private final AuthTokensService authTokensService;
    private final TokenUtils tokenUtils;
    private final RestClient restClient;

    public LoginOAuth2Controller(AuthTokensService authTokensService, TokenUtils tokenUtils, RestClient restClient) {
        this.authTokensService = authTokensService;
        this.tokenUtils = tokenUtils;
        this.restClient = restClient;
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
        ProviderUserDataResponse providerUserData = this.restClient.getProviderUserDataFromKey(key);
        if (providerUserData == null) {
            log.error("Getting provider-user-data: " + "ERROR");
            return returnError(response, frontendRedirectUri, "provider_user_data_not_found");
        }
        log.info("Getting provider-user-data: " + "OK");

        // 2. Searching for user in user-service with this linked account id and provider
        // TODO: dodać obsługę tokenu który trzeba przesłać w headerze, ponieważ teraz jest to publiczny endpoint
        //  i każdy może go wywołać i uzyskać dane użytkownika
        CreateUserResponse getUserResponse = this.restClient.getUserFromProvider(
                new GetUserFromProviderRequest(authProvider, providerUserData.providerUserId()));
        if (getUserResponse == null) {
            log.error("Getting user in user-service: " + "ERROR");
            return returnError(response, frontendRedirectUri, "user_not_found");
        }
        log.info("Getting user in user-service: " + "OK");

        response.setHeader("Location", frontendRedirectUri + "?user=" + getUserResponse.name());
        response.setStatus(HttpStatus.FOUND.value());
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    private ResponseEntity<Void> returnError(HttpServletResponse response, String redirectUri, String error) {
        response.setHeader("Location", redirectUri + "?error=" + error);
        response.setStatus(HttpStatus.FOUND.value());
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }
}
