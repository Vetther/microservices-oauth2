package pl.owolny.userservice.linkedaccount;

import pl.owolny.userservice.authprovider.AuthProvider;

public record LinkedAccountDTO(Long id, AuthProvider authProvider, String providerUserId,
                               String providerUserEmail, String providerUserName) {}
