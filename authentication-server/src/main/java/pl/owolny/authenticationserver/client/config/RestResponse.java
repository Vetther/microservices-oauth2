package pl.owolny.authenticationserver.client.config;

import java.util.Optional;

public record RestResponse<T>(boolean success, String error, Optional<T> data) {

}
