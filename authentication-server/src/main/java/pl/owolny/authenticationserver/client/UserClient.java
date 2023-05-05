package pl.owolny.authenticationserver.client;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.owolny.authenticationserver.authprovider.AuthProvider;
import pl.owolny.authenticationserver.client.config.RestResponse;
import pl.owolny.authenticationserver.controller.ResponseBodyError;
import pl.owolny.authenticationserver.client.model.UserDTO;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;

    /**
     * Creates user in user-service
     * @return null if user was not created
     */
    public RestResponse<UserDTO> createUser(CreateUserRequest createUserRequest) {
        ResponseEntity<UserDTO> restUserEntity = restTemplate.exchange(
                "http://user-service/users/create",
                HttpMethod.POST,
                new HttpEntity<>(createUserRequest),
                UserDTO.class
        );

        if (restUserEntity.getStatusCode() == HttpStatus.CONFLICT) {
            return new RestResponse<>(false, ResponseBodyError.USER_ALREADY_EXISTS.name(), Optional.empty());
        }

        if (restUserEntity.getStatusCode() == HttpStatus.OK && restUserEntity.getBody() != null) {
            return new RestResponse<>(true, StringUtils.EMPTY, Optional.of(restUserEntity.getBody()));
        }

        return new RestResponse<>(false, ResponseBodyError.USER_CREATION_FAILED.name(), Optional.empty());
    }

    public static CreateUserRequest createUserRequest(SecretKeyClient.ProviderUserDataResponse providerUserData, AuthProvider authProvider) {
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

    public record CreateUserRequest(
            String name,
            String username,
            String password,
            String email,
            String imageUrl,
            LinkedAccountRequest provider
    ) {
    }

    public record LinkedAccountRequest (
            AuthProvider authProvider,
            String providerUserId,
            String providerUserEmail,
            String providerUserName
    ) {
    }
}
