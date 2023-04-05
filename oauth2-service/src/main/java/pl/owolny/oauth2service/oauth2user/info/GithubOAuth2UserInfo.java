package pl.owolny.oauth2service.oauth2user.info;

import pl.owolny.oauth2service.authprovider.AuthProvider;
import pl.owolny.oauth2service.exception.OAuth2AuthenticationProcessingException;
import pl.owolny.oauth2service.oauth2user.OAuth2UserInfo;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

    private static final String GITHUB_API_ENDPOINT = "https://api.github.com/user/emails";
    private final OAuth2AccessToken oAuth2AccessToken;

    public GithubOAuth2UserInfo(Map<String, Object> attributes, OAuth2AccessToken oAuth2AccessToken) {
        super(AuthProvider.GITHUB, attributes);
        this.oAuth2AccessToken = oAuth2AccessToken;
    }

    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        GithubOAuth2ApiClient githubApi = new GithubOAuth2ApiClient();
        GithubOAuth2ApiClient.Email[] userEmail = githubApi.getUserEmail(this.oAuth2AccessToken.getTokenValue());
        return Arrays.stream(userEmail)
                .filter(GithubOAuth2ApiClient.Email::isPrimary)
                .findFirst()
                .orElseThrow(() -> new OAuth2AuthenticationProcessingException("EMAIL_NOT_FOUND"))
                .getEmail();
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }

    public static class GithubOAuth2ApiClient {

        public Email[] getUserEmail(String token) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Token " + token);
            HttpEntity<String> request = new HttpEntity<>(headers);
            return new RestTemplate().exchange(GITHUB_API_ENDPOINT, HttpMethod.GET, request, Email[].class).getBody();
        }

        static class Email {
            private String email;
            private boolean primary;
            public String getEmail() {
                return email;
            }
            public void setEmail(String email) {
                this.email = email;
            }
            public boolean isPrimary() {
                return primary;
            }
            public void setPrimary(boolean primary) {
                this.primary = primary;
            }
        }
    }
}