package pl.owolny.oauth2service.oauth2user.info;

import lombok.Getter;
import pl.owolny.oauth2service.authprovider.AuthProvider;
import pl.owolny.oauth2service.oauth2user.OAuth2UserInfo;

import java.util.Map;

@Getter
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(AuthProvider.GOOGLE, attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}