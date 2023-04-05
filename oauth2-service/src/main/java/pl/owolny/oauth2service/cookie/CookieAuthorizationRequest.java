package pl.owolny.oauth2service.cookie;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import pl.owolny.oauth2service.utils.CookieUtils;

import java.util.Arrays;

@Component
public class CookieAuthorizationRequest implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH2_COOKIE_PREFIX = "oauth2_cookie_";

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String OAUTH2_REDIRECT_URI_COOKIE_NAME = "redirect_uri";

    private static final int cookieExpireSeconds = 300;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {

        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }

        // Necessary cookies
        CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtils.serialize(authorizationRequest), cookieExpireSeconds);
        CookieUtils.addCookie(response, OAUTH2_REDIRECT_URI_COOKIE_NAME, getRedirectUriParam(request), cookieExpireSeconds);

        // Additional cookies
        request.getParameterMap().forEach((key, value) -> {
            if (!key.equalsIgnoreCase(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME) && !key.equalsIgnoreCase(OAUTH2_REDIRECT_URI_COOKIE_NAME)) {
                CookieUtils.addCookie(response, OAUTH2_COOKIE_PREFIX + key, Arrays.toString(value), cookieExpireSeconds);
            }
        });
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, OAUTH2_REDIRECT_URI_COOKIE_NAME);

        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().startsWith(OAUTH2_COOKIE_PREFIX))
                .forEach(cookie -> CookieUtils.deleteCookie(request, response, cookie.getName()));
    }

    private String getRedirectUriParam(HttpServletRequest request) {
        String redirectUri = request.getParameter(OAUTH2_REDIRECT_URI_COOKIE_NAME);
        if (StringUtils.isBlank(redirectUri)) {
            return "/";
        }
        return redirectUri;
    }
}