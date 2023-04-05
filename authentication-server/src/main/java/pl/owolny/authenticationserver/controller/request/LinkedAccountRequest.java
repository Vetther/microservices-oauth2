package pl.owolny.authenticationserver.controller.request;

import pl.owolny.authenticationserver.authprovider.AuthProvider;

public record LinkedAccountRequest (
        AuthProvider authProvider,
        String providerUserId,
        String providerUserEmail,
        String providerUserName
) {
}