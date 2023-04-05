package pl.owolny.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.owolny.userservice.authprovider.AuthProvider;
import pl.owolny.userservice.controller.request.CreateUserRequest;
import pl.owolny.userservice.controller.request.LinkedAccountRequest;
import pl.owolny.userservice.controller.request.UpdateLinkRequest;
import pl.owolny.userservice.linkedaccount.LinkedAccountDTO;
import pl.owolny.userservice.user.UserDTO;
import pl.owolny.userservice.linkedaccount.LinkedAccount;
import pl.owolny.userservice.linkedaccount.LinkedAccountDTOMapper;
import pl.owolny.userservice.user.User;
import pl.owolny.userservice.user.UserDTOMapper;
import pl.owolny.userservice.user.UserService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserDTOMapper userDTOMapper;
    private final LinkedAccountDTOMapper linkedAccountDTOMapper;

    public UserController(UserService userService, UserDTOMapper userDTOMapper, LinkedAccountDTOMapper linkedAccountDTOMapper) {
        this.userService = userService;
        this.userDTOMapper = userDTOMapper;
        this.linkedAccountDTOMapper = linkedAccountDTOMapper;
    }

    @GetMapping()
    public ResponseEntity<Set<UserDTO>> getUsers() {
        return ResponseEntity.ok(this.userService.getUsers().stream()
                .map(this.userDTOMapper)
                .collect(Collectors.toSet()));
    }

    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest createUserRequest) {

        User user = userService.createUser(
                createUserRequest.name(),
                createUserRequest.email(),
                createUserRequest.password(),
                createUserRequest.imageUrl(),
                new HashSet<>(),
                false
        );

        if (createUserRequest.provider().authProvider() != null
        && createUserRequest.provider().providerUserId() != null) {
            this.userService.addLink(
                    user.getId(),
                    createUserRequest.provider().authProvider(),
                    createUserRequest.provider().providerUserId(),
                    createUserRequest.provider().providerUserEmail(),
                    createUserRequest.provider().providerUserName()
            );
        }

        return ResponseEntity.ok(this.userDTOMapper.apply(user));
    }

    @PostMapping("/{userId}/links/create")
    public ResponseEntity<LinkedAccountDTO> createLink(@PathVariable Long userId, @RequestBody LinkedAccountRequest linkedAccountRequest) {
        LinkedAccount linkedAccount = this.userService.addLink(
                userId,
                linkedAccountRequest.authProvider(),
                linkedAccountRequest.providerUserId(),
                linkedAccountRequest.providerUserEmail(),
                linkedAccountRequest.providerUserName()
        );
        return ResponseEntity.ok(this.linkedAccountDTOMapper.apply(linkedAccount));
    }

    @GetMapping("/{userId}/links")
    public ResponseEntity<Set<LinkedAccountDTO>> getLink(@PathVariable Long userId) {
        Set<LinkedAccount> links = userService.getLinks(userId);
        return ResponseEntity.ok(links.stream()
                .map(this.linkedAccountDTOMapper)
                .collect(Collectors.toSet()));
    }

    @DeleteMapping("/{userId}/links")
    public ResponseEntity<Void> removeLink(@PathVariable Long userId, @RequestParam AuthProvider authProvider) {
        userService.removeLink(userId, authProvider);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/links/{authProvider}")
    public ResponseEntity<LinkedAccountDTO> getLink(@PathVariable Long userId, @PathVariable AuthProvider authProvider) {
        Optional<LinkedAccount> link = userService.getLink(userId, authProvider);
        return link.map(linkedAccount -> ResponseEntity.ok(this.linkedAccountDTOMapper.apply(linkedAccount))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/links/{authProvider}")
    public ResponseEntity<Void> updateLink(@PathVariable Long userId, @PathVariable AuthProvider authProvider, @RequestBody UpdateLinkRequest updateLinkRequest) {
        userService.updateLink(userId, authProvider, updateLinkRequest.providerUserEmail(), updateLinkRequest.providerUserName());
        return ResponseEntity.noContent().build();
    }

}
