package pl.owolny.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.owolny.userservice.authprovider.AuthProvider;
import pl.owolny.userservice.controller.request.GetUserFromProviderRequest;
import pl.owolny.userservice.linkedaccount.LinkedAccountDTOMapper;
import pl.owolny.userservice.user.User;
import pl.owolny.userservice.user.UserDTO;
import pl.owolny.userservice.user.UserDTOMapper;
import pl.owolny.userservice.user.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/admin/users")
public class UserAdminController {

    private final UserService userService;
    private final UserDTOMapper userDTOMapper;
    private final LinkedAccountDTOMapper linkedAccountDTOMapper;

    public UserAdminController(UserService userService, UserDTOMapper userDTOMapper, LinkedAccountDTOMapper linkedAccountDTOMapper) {
        this.userService = userService;
        this.userDTOMapper = userDTOMapper;
        this.linkedAccountDTOMapper = linkedAccountDTOMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(this.userDTOMapper.apply(this.userService.getUser(id)));
    }

    @GetMapping("/provider/{provider}/{providerUserId}")
    public ResponseEntity<UserDTO> getUserFromProvider(
            @PathVariable AuthProvider provider,
            @PathVariable String providerUserId) {

        Optional<User> user = this.userService.getUserFromProvider(provider, providerUserId);

        return user.map(value -> ResponseEntity.ok(this.userDTOMapper.apply(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
