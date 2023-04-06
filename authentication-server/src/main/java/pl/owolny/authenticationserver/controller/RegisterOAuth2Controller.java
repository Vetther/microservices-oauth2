package pl.owolny.authenticationserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.owolny.authenticationserver.authprovider.AuthProvider;
import pl.owolny.authenticationserver.controller.request.CreateUserRequest;
import pl.owolny.authenticationserver.controller.request.LinkedAccountRequest;
import pl.owolny.authenticationserver.controller.response.CreateUserResponse;
import pl.owolny.authenticationserver.controller.response.ProviderUserDataResponse;
import pl.owolny.authenticationserver.utils.OAuth2RoutesUtils;

@RestController
@RequestMapping("/register/oauth2")
@Slf4j
public class RegisterOAuth2Controller {

    private final RestTemplate restTemplate;

    public RegisterOAuth2Controller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
        ProviderUserDataResponse providerUserData = getProviderUserData(key);
        if (providerUserData == null) {
            log.error("Getting provider-user-data: " + "ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        log.info("Getting provider-user-data: " + "OK");

        // 2. Creating user in user-service
        CreateUserResponse createUserResponse = createUser(createUserRequest(providerUserData, authProvider));
        if (createUserResponse == null) {
            log.error("Creating user in user-service: " + "ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        log.info("Creating user in user-service: " + "OK");

        // 3. Redirecting to frontend
        response.setHeader("Location", frontendRedirectUri + "?token=" + "niby-tutaj-token-ale-pewnie-secret-do-jwt");
        response.setStatus(HttpStatus.FOUND.value());
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    private ProviderUserDataResponse getProviderUserData(String key) {
        ResponseEntity<ProviderUserDataResponse> restKeyEntity = restTemplate.exchange(
                "http://oauth2-service/keys/" + key,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                ProviderUserDataResponse.class);
        return (restKeyEntity.getStatusCode() == HttpStatus.OK) ? restKeyEntity.getBody() : null;
    }

    private CreateUserRequest createUserRequest(ProviderUserDataResponse providerUserData, AuthProvider authProvider) {
        return new CreateUserRequest(
                providerUserData.getProviderUserName(),
                null,
                providerUserData.getProviderUserEmail(),
                providerUserData.getProviderUserImageUrl(),
                new LinkedAccountRequest(
                        authProvider,
                        providerUserData.getProviderUserId(),
                        providerUserData.getProviderUserEmail(),
                        providerUserData.getProviderUserName()
                )
        );
    }

    private CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        ResponseEntity<CreateUserResponse> restUserEntity = restTemplate.postForEntity(
                "http://user-service/users/create",
                createUserRequest,
                CreateUserResponse.class
        );
        return (restUserEntity.getStatusCode() == HttpStatus.OK) ? restUserEntity.getBody() : null;
    }
}
