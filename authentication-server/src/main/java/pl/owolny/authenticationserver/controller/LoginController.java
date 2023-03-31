package pl.owolny.authenticationserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.owolny.authenticationserver.login.LoginDto;

import java.io.IOException;

@RestController
@RequestMapping("/login")
public class LoginController {

    @PostMapping()
    public ResponseEntity<String> loginController(@RequestBody @Valid LoginDto loginDto) {
        return ResponseEntity.ok("User logged successfully.");
    }

    @GetMapping("/oauth2")
    public void loginOAuth2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUrl = String.format("http://%s:%d/auth/register/oauth2/callback", request.getServerName(), request.getServerPort());
        response.sendRedirect("/oauth2/authorize/google?redirect_uri=" + redirectUrl);
    }

    @GetMapping("/oauth2/callback")
    public void loginOAuth2Callback(HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:3000");
    }
}