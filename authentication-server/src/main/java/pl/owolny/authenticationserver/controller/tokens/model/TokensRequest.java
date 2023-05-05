package pl.owolny.authenticationserver.controller.tokens.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokensRequest(
        @JsonProperty("secret_key") String secretKey) {
}
