package pl.owolny.oauth2service.oauth2user.info;

import pl.owolny.oauth2service.authprovider.AuthProvider;
import pl.owolny.oauth2service.oauth2user.OAuth2UserInfo;

import java.util.Map;

public class DiscordOAuth2UserInfo extends OAuth2UserInfo {

    public DiscordOAuth2UserInfo(Map<String, Object> attributes) {
        super(AuthProvider.DISCORD, attributes);
    }

    @Override
    public String getId() {return (String) attributes.get("id");}

    @Override
    public String getName() {
        return (String) attributes.get("username");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return "https://cdn.discordapp.com/avatars/" + attributes.get("id") + "/" + attributes.get("avatar");
    }
}