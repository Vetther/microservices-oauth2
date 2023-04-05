package pl.owolny.authenticationserver.utils;

import org.springframework.web.util.UriComponentsBuilder;
import pl.owolny.authenticationserver.authprovider.AuthProvider;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OAuth2RoutesUtils {

    public static String getOAuth2Route(AuthProvider authProvider, String callbackUri, String frontendUri) {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("/");

        if (authProvider == AuthProvider.GOOGLE) {
            uriComponentsBuilder = UriComponentsBuilder.fromUriString("/oauth2/authorize/google");
        }
        if (authProvider == AuthProvider.FACEBOOK) {
            uriComponentsBuilder = UriComponentsBuilder.fromUriString("/oauth2/authorize/facebook");
        }
        if (authProvider == AuthProvider.GITHUB) {
            uriComponentsBuilder = UriComponentsBuilder.fromUriString("/oauth2/authorize/github");
        }
        if (authProvider == AuthProvider.DISCORD) {
            uriComponentsBuilder = UriComponentsBuilder.fromUriString("/oauth2/authorize/discord");
        }
        if (authProvider == AuthProvider.TWITCH) {
            uriComponentsBuilder = UriComponentsBuilder.fromUriString("/oauth2/authorize/twitch");
        }

        uriComponentsBuilder.queryParam("redirect_uri", URLEncoder.encode(callbackUri, StandardCharsets.UTF_8));
        uriComponentsBuilder.queryParam("frontend_uri", URLEncoder.encode(frontendUri, StandardCharsets.UTF_8));

        return uriComponentsBuilder.build().toUriString();
    }
}
