package pl.owolny.authenticationserver.controller.register.request;

public record CreateUserRequest(
        String name,
        String username,
        String password,
        String email,
        String imageUrl,
        LinkedAccountRequest provider
) {

}