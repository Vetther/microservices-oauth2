package pl.owolny.authenticationserver.controller.register.response;

import lombok.Getter;
import lombok.Setter;

public record ProviderUserDataResponse(
        String key,
        String providerUserId,
        String providerUserName,
        String providerUserUsername,
        String providerUserEmail,
        String providerUserImageUrl) {
}