package pl.owolny.oauth2service.oauth2user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.owolny.oauth2service.authprovider.AuthProvider;

import java.util.Collection;
import java.util.Map;

@Getter
public class OAuth2UserPrincipal implements OAuth2User {

    private final String username;
    private final String email;
    private final String id;
    private final String imageUrl;
    private final AuthProvider authProvider;
    private final Map<String, Object> attributes;

    private OAuth2UserPrincipal(String username, String email, String id, String imageUrl, AuthProvider authProvider, Map<String, Object> attributes) {
        this.username = username;
        this.email = email;
        this.id = id;
        this.imageUrl = imageUrl;
        this.authProvider = authProvider;
        this.attributes = attributes;
    }

    public static OAuth2UserPrincipal create(String username, String email, String id, String imageUrl, AuthProvider authProvider, Map<String, Object> attributes) {
        return new OAuth2UserPrincipal(username, email, id, imageUrl, authProvider, attributes);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return this.username;
    }
}
