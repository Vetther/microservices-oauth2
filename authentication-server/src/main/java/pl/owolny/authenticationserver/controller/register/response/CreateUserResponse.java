package pl.owolny.authenticationserver.controller.register.response;

import java.util.List;

public record CreateUserResponse(String name, String username, String email, String imageUrl, List<String> roles) {}

