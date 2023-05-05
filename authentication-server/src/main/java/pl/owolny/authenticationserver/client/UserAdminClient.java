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
public class UserAdminClient {

    private final RestTemplate restTemplate;

    /**
     * Gets user from user-service by provider and providerUserId
     * @return null if user was not found
     */
    public RestResponse<UserDTO> getUserFromProvider(UserFromProviderRequest userFromProviderRequest) {
        ResponseEntity<UserDTO> restUserEntity = restTemplate.exchange(
                "http://user-service/admin/users/provider/" + userFromProviderRequest.provider().name() + "/" + userFromProviderRequest.providerUserId(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                UserDTO.class
        );

        if (restUserEntity.getStatusCode() == HttpStatus.OK && restUserEntity.getBody() != null) {
            return new RestResponse<>(true, StringUtils.EMPTY, Optional.of(restUserEntity.getBody()));
        }

        return new RestResponse<>(false, ResponseBodyError.PROVIDER_USER_DATA_NOT_FOUND.name(), Optional.empty());
    }

    public record UserFromProviderRequest(AuthProvider provider, String providerUserId) {
    }
}
