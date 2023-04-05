package pl.owolny.authenticationserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
import pl.owolny.authenticationserver.controller.response.KeyResponse;
import pl.owolny.authenticationserver.registration.RegistrationDto;
import pl.owolny.authenticationserver.utils.OAuth2RoutesUtils;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private final RestTemplate restTemplate;

    public RegisterController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping()
    public ResponseEntity<String> registerCredentials(@RequestBody @Valid RegistrationDto registrationDto) {
        return ResponseEntity.ok("User registered successfully.");
    }

    @GetMapping("/oauth2/{authProvider}")
    public void registerOAuth2(
            HttpServletResponse response,
            @PathVariable AuthProvider authProvider,
            @RequestParam(required = false, name = "redirect_uri") String frontend_redirect_uri
    ) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String callbackUrl = baseUrl + "/auth/register/oauth2/callback/" + authProvider;
        String providerUrl = OAuth2RoutesUtils.getOAuth2Route(authProvider, callbackUrl, frontend_redirect_uri);

        response.setHeader("Location", providerUrl);
        response.setStatus(302);
    }

    @GetMapping("/oauth2/callback/{authProvider}")
    public void registerOAuth2Callback(HttpServletResponse response,
                                       HttpServletRequest request,
                                       @RequestParam String key,
                                       @RequestParam(required = false, name = "frontend_uri") String frontend_redirect_uri,
                                       @PathVariable AuthProvider authProvider) {

        System.out.println("NOWY CALLBACK");
        System.out.println("PARAMS: " + request.getQueryString());
        System.out.println("KLUCZ: " + key);
        System.out.println("FRONTEND_URI: " + frontend_redirect_uri);

        ResponseEntity<KeyResponse> restKeyEntity = restTemplate.exchange(
                "http://oauth2-service/keys/" + key,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                KeyResponse.class);

        if (restKeyEntity.getStatusCode() != HttpStatus.OK || restKeyEntity.getBody() == null) {
            System.out.println("Blad");
            return;
        }

        KeyResponse keyResponse = restKeyEntity.getBody();

        System.out.println("RESPONSE - OK!");

        System.out.println("ID:" + keyResponse.getProviderUserId());
        System.out.println("EMAIL:" + keyResponse.getProviderUserEmail());
        System.out.println("NAME:" + keyResponse.getProviderUserName());
        System.out.println("IMAGE:" + keyResponse.getProviderUserImageUrl());

        CreateUserRequest createUserRequest = new CreateUserRequest(
                keyResponse.getProviderUserName(),
                null,
                keyResponse.getProviderUserEmail(),
                keyResponse.getProviderUserImageUrl(),
                new LinkedAccountRequest(
                        authProvider,
                        keyResponse.getProviderUserId(),
                        keyResponse.getProviderUserEmail(),
                        keyResponse.getProviderUserName()
                )
        );

        ResponseEntity<CreateUserResponse> restUserEntity = restTemplate.postForEntity(
                "http://user-service/users/create",
                createUserRequest,
                CreateUserResponse.class
        );

        System.out.println("Creating user...");
        if (restUserEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("User created with name " + restUserEntity.getBody().name() + "!");
        } else {
            System.out.println("Response Error");
        }

        response.setHeader("Location", frontend_redirect_uri + "?token=" + "niby-tutaj-token-ale-pewnie-secret-do-jwt");
        response.setStatus(302);
    }
}
