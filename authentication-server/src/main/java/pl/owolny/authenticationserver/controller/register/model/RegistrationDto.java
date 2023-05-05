package pl.owolny.authenticationserver.controller.register.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationDto {

    @NotBlank(message = "Username is required.")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters long.")
    private String username;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 32, message = "Password must be between 6 and 30 characters long.")
    private String password;

}
