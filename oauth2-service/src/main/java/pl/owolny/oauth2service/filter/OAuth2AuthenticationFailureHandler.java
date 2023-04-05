package pl.owolny.oauth2service.filter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.owolny.oauth2service.cookie.CookieAuthorizationRequest;
import pl.owolny.oauth2service.utils.CookieUtils;

import java.io.IOException;

import static pl.owolny.oauth2service.cookie.CookieAuthorizationRequest.OAUTH2_REDIRECT_URI_COOKIE_NAME;

@AllArgsConstructor
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final CookieAuthorizationRequest OAuth2CookieAuthorizationRequest;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        String targetUrl = CookieUtils.getCookie(request, OAUTH2_REDIRECT_URI_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(("/"));

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage().replace("[", "").replace("]", ""))
                .build().toUriString();

        OAuth2CookieAuthorizationRequest.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}