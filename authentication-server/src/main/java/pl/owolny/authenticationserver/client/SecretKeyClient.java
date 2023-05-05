package pl.owolny.authenticationserver.client;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.owolny.authenticationserver.client.config.RestResponse;
import pl.owolny.authenticationserver.controller.ResponseBodyError;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SecretKeyClient {

    private final RestTemplate restTemplate;

    /**
     * Gets user data (from provider like google) from oauth2-service after registration/login
     * @return null if user data was not found
     */
    public RestResponse<ProviderUserDataResponse> getProviderUserDataFromSecretKey(String secretKey) {
        ResponseEntity<ProviderUserDataResponse> restKeyEntity = restTemplate.exchange(
                "http://oauth2-service/keys/" + secretKey,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                ProviderUserDataResponse.class);

        if (restKeyEntity.getStatusCode() == HttpStatus.OK && restKeyEntity.getBody() != null) {
            return new RestResponse<>(true, null, Optional.of(restKeyEntity.getBody()));
        }

        // TODO: poprawic i rozdzielic blad od USER_DATA_NOT_FOUND a NIE_UDALO_SIE_PRZETWORZYC_ZADANIA
        return new RestResponse<>(false, ResponseBodyError.USER_DATA_NOT_FOUND.name(), Optional.empty());
    }

    public record ProviderUserDataResponse(
            String secretKey,
            String providerUserId,
            String providerUserName,
            String providerUserUsername,
            String providerUserEmail,
            String providerUserImageUrl) {
    }
}
