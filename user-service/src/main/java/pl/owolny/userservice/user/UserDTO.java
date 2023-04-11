package pl.owolny.userservice.user;

import java.util.List;

public record UserDTO(String name, String username, String email, String imageUrl, List<String> roles) {}