package pl.owolny.authenticationserver.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDto {

    @NotBlank(message = "Username or email is required.")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters long.")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 32, message = "Password must be between 6 and 30 characters long.")
    private String password;

}
