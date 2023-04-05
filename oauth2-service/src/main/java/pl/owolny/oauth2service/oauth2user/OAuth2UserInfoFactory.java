package pl.owolny.oauth2service.oauth2user;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import pl.owolny.oauth2service.authprovider.AuthProvider;
import pl.owolny.oauth2service.oauth2user.info.*;

import java.util.Map;

@AllArgsConstructor
@Configuration
public class OAuth2UserInfoFactory {

    private final Environment environment;

    public OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes, OAuth2AccessToken oAuth2AccessToken) {

        if(provider.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        if(provider.toUpperCase().equalsIgnoreCase(AuthProvider.DISCORD.name())) {
            return new DiscordOAuth2UserInfo(attributes);
        }
        if(provider.toUpperCase().equalsIgnoreCase(AuthProvider.FACEBOOK.name())) {
            return new FacebookOAuth2UserInfo(attributes);
        }
        if(provider.toUpperCase().equalsIgnoreCase(AuthProvider.GITHUB.name())) {
            return new GithubOAuth2UserInfo(attributes, oAuth2AccessToken);
        }
        if(provider.toUpperCase().equalsIgnoreCase(AuthProvider.TWITCH.name())) {
            return new TwitchOAuth2UserInfo(attributes, oAuth2AccessToken, environment);
        }

        throw new OAuth2AuthenticationException("Sorry! Login with " + provider + " is not supported yet.");
    }
}