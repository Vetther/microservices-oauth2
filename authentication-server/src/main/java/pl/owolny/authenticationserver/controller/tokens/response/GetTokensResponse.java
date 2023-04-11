package pl.owolny.authenticationserver.controller.tokens.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetTokensResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken) {
}
