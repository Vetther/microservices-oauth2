package pl.owolny.authenticationserver.controller.tokens.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokensResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken) {
}
