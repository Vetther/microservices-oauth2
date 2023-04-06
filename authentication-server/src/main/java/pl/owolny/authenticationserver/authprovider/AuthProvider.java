package pl.owolny.authenticationserver.authprovider;

public enum AuthProvider {
    GOOGLE,
    FACEBOOK,
    GITHUB,
    CREDENTIALS,
    DISCORD,
    TWITCH;

    public static AuthProvider fromString(String value) {
        if (value != null) {
            for (AuthProvider provider : AuthProvider.values()) {
                if (value.equalsIgnoreCase(provider.name())) {
                    return provider;
                }
            }
        }
        throw new IllegalArgumentException("No constant with value " + value + " found for AuthProvider enum");
    }
}