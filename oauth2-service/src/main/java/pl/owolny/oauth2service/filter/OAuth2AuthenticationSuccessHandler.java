package pl.owolny.oauth2service.filter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import pl.owolny.oauth2service.cookie.CookieAuthorizationRequest;
import pl.owolny.oauth2service.oauth2user.OAuth2UserPrincipal;
import pl.owolny.oauth2service.redis.provideruserdata.ProviderUserData;
import pl.owolny.oauth2service.redis.provideruserdata.ProviderUserDataService;
import pl.owolny.oauth2service.utils.CookieUtils;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import static pl.owolny.oauth2service.cookie.CookieAuthorizationRequest.OAUTH2_COOKIE_PREFIX;
import static pl.owolny.oauth2service.cookie.CookieAuthorizationRequest.OAUTH2_REDIRECT_URI_COOKIE_NAME;

@AllArgsConstructor
@Configuration
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CookieAuthorizationRequest OAuth2CookieAuthorizationRequest;
    private final ProviderUserDataService providerUserDataService;

    @Value("${app.oauth2.allowedRedirectUris}")
    private Set<String> allowedRedirectUris;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // Get the redirect URI from the cookie
        Optional<String> redirectUri = CookieUtils.getCookie(request, OAUTH2_REDIRECT_URI_COOKIE_NAME)
                .map(Cookie::getValue);

        // Get all cookies with the prefix "oauth2_" and add them to the redirect parameters
        MultiValueMap<String, String> additionalParams = new LinkedMultiValueMap<>();
        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().startsWith(OAUTH2_COOKIE_PREFIX))
                .forEach(cookie -> additionalParams.add(
                        cookie.getName().substring(OAUTH2_COOKIE_PREFIX.length()),
                        cookie.getValue().replaceAll("\\[|\\]", ""))
                );

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("UNAUTHORIZED_REDIRECT_URI");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        OAuth2UserPrincipal user = (OAuth2UserPrincipal) authentication.getPrincipal();

        ProviderUserData providerUserData = new ProviderUserData(
                generateRandomKey(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getImageUrl()
        );
        this.providerUserDataService.save(providerUserData);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("key", providerUserData.getKey())
                .queryParams(additionalParams)
                .build().toUriString();
    }

    private String generateRandomKey() {
        return UUID.randomUUID().toString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        OAuth2CookieAuthorizationRequest.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return allowedRedirectUris
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port, no paths
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}