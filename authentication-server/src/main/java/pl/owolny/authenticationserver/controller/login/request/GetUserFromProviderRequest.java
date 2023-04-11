package pl.owolny.authenticationserver.controller.login.request;

import pl.owolny.authenticationserver.authprovider.AuthProvider;

public record GetUserFromProviderRequest(AuthProvider provider, String providerUserId) {
}
