package pl.owolny.userservice.controller.request;

public record CreateUserRequest(
        String name,
        String password,
        String email,
        String imageUrl,
        LinkedAccountRequest provider
) {

}

