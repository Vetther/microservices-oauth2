package pl.owolny.authenticationserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.owolny.authenticationserver.registration.RegistrationDto;

import java.io.IOException;
@RestController
@RequestMapping("/register")
public class RegisterController {

    @PostMapping()
    public ResponseEntity<String> registerCredentials(@RequestBody @Valid RegistrationDto registrationDto) {
        return ResponseEntity.ok("User registered successfully.");
    }

    @GetMapping("/oauth2")
    public void registerOAuth2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUrl = String.format("http://%s:%d/auth/register/oauth2/callback", request.getServerName(), request.getServerPort());
        response.sendRedirect("/oauth2/authorize/google?redirect_uri=" + redirectUrl);
    }

    @GetMapping("/oauth2/callback")
    public void registerOAuth2Callback(HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:3000");
    }
}
