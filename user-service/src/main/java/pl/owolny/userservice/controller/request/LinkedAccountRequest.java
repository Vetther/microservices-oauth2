package pl.owolny.userservice.controller.request;

import pl.owolny.userservice.authprovider.AuthProvider;

public record LinkedAccountRequest (
        AuthProvider authProvider,
        String providerUserId,
        String providerUserEmail,
        String providerUserName
) {
}