package pl.owolny.authenticationserver.controller.request;

public record CreateUserRequest(
        String name,
        String password,
        String email,
        String imageUrl,
        LinkedAccountRequest provider
) {

}