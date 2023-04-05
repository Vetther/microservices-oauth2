package pl.owolny.authenticationserver.controller.response;

import java.util.List;

public record CreateUserResponse(String name, String email, String imageUrl, List<String> roles) {}

