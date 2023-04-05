package pl.owolny.oauth2service.oauth2user;

import pl.owolny.oauth2service.authprovider.AuthProvider;

import java.util.Map;

public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;
    protected AuthProvider provider;

    public OAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public AuthProvider getProvider() {
        return this.provider;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();
}