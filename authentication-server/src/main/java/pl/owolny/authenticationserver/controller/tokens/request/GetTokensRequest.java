package pl.owolny.authenticationserver.controller.tokens.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetTokensRequest(
        @JsonProperty("secret_key") String secretKey) {
}
