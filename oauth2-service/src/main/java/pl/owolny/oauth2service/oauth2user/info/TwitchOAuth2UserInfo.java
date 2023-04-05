package pl.owolny.oauth2service.oauth2user.info;

import lombok.Data;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;
import pl.owolny.oauth2service.authprovider.AuthProvider;
import pl.owolny.oauth2service.oauth2user.OAuth2UserInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TwitchOAuth2UserInfo extends OAuth2UserInfo {

    private final static String TWITCH_API_ENDPOINT = "https://api.twitch.tv/helix/users";
    private final TwitchUser twitchUser;

    public TwitchOAuth2UserInfo(Map<String, Object> attributes, OAuth2AccessToken oAuth2AccessToken, Environment environment) {
        super(AuthProvider.TWITCH, attributes);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(oAuth2AccessToken.getTokenValue());
        headers.add("Client-Id", environment.getProperty("spring.security.oauth2.client.registration.twitch.client-id"));
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity<TwitchUserResponse> response = new RestTemplate().exchange(TWITCH_API_ENDPOINT, HttpMethod.GET, entity, TwitchUserResponse.class);
        this.twitchUser = response.getBody().getData().get(0);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return this.twitchUser.getLogin();
    }

    @Override
    public String getEmail() {
        return this.twitchUser.getEmail();
    }

    @Override
    public String getImageUrl() {
        return this.twitchUser.getProfile_image_url();
    }

    @Data
    public static class TwitchUserResponse {
        private List<TwitchUser> data;
    }

    @Data
    public static class TwitchUser {
        private Long id;
        private String login;
        private String display_name;
        private String type;
        private String broadcaster_type;
        private String description;
        private String profile_image_url;
        private String offline_image_url;
        private Long view_count;
        private String email;
        private LocalDateTime created_at;
    }
}