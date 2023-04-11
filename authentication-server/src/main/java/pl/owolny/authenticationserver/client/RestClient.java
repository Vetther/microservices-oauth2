package pl.owolny.authenticationserver.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.owolny.authenticationserver.controller.login.request.GetUserFromProviderRequest;
import pl.owolny.authenticationserver.controller.register.request.CreateUserRequest;
import pl.owolny.authenticationserver.controller.register.response.CreateUserResponse;
import pl.owolny.authenticationserver.controller.register.response.ProviderUserDataResponse;

@Service
public class RestClient {

    private final RestTemplate restTemplate;

    public RestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Creates user in user-service
     * @param createUserRequest
     * @return null if user was not created
     */
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        try {
            ResponseEntity<CreateUserResponse> restUserEntity = restTemplate.exchange(
                    "http://user-service/users/create",
                    HttpMethod.POST,
                    new HttpEntity<>(createUserRequest),
                    CreateUserResponse.class
            );
            return (restUserEntity.getStatusCode() == HttpStatus.OK) ? restUserEntity.getBody() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets user from user-service by provider and providerUserId
     * @param getUserFromProviderRequest
     * @return null if user was not found
     */
    public CreateUserResponse getUserFromProvider(GetUserFromProviderRequest getUserFromProviderRequest) {
        try {
            ResponseEntity<CreateUserResponse> restUserEntity = restTemplate.exchange(
                    "http://user-service/admin/users/provider/" + getUserFromProviderRequest.provider().name() + "/" + getUserFromProviderRequest.providerUserId(),
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    CreateUserResponse.class
            );
            return (restUserEntity.getStatusCode() == HttpStatus.OK) ? restUserEntity.getBody() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets user data (from provider like google) from oauth2-service after registration/login
     * @param key
     * @return null if user data was not found
     */
    public ProviderUserDataResponse getProviderUserDataFromKey(String key) {
        try {
            ResponseEntity<ProviderUserDataResponse> restKeyEntity = restTemplate.exchange(
                    "http://oauth2-service/keys/" + key,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    ProviderUserDataResponse.class);
            return (restKeyEntity.getStatusCode() == HttpStatus.OK) ? restKeyEntity.getBody() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
