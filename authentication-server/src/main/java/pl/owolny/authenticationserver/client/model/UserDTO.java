package pl.owolny.authenticationserver.client.model;

import java.util.List;

public record UserDTO(String name, String username, String email, String imageUrl, List<String> roles) {}

