package pl.owolny.userservice.user;

import jakarta.ws.rs.core.Link;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.owolny.userservice.authprovider.AuthProvider;
import pl.owolny.userservice.exception.NotFoundException;
import pl.owolny.userservice.linkedaccount.LinkedAccount;
import pl.owolny.userservice.linkedaccount.LinkedAccountService;
import pl.owolny.userservice.role.Role;
import pl.owolny.userservice.role.RoleService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final LinkedAccountService linkedAccountService;

    // TODO: checki czy nazwa/email sie nie powtarza, sprawdzanie czy nie podpinamy po raz drugi tego samego providera i pewnie jeszcze mnostwo innych poprawek

    public User createUser(String name, String email, String password, String imageUrl, Set<LinkedAccount> linkedAccounts, boolean emailVerification) {
        return this.userRepository.save(
                new User(
                        null,
                        name,
                        password,
                        email,
                        imageUrl,
                        linkedAccounts,
                        new HashSet<>(),
                        emailVerification
                )
        );
    }

    public LinkedAccount addLink(Long userId, AuthProvider authProvider, String providerUserId, String providerUserEmail, String providerUserName) {
        LinkedAccount linkedAccount = this.linkedAccountService.createLink(authProvider, providerUserId, providerUserEmail, providerUserName);
        User user = this.getUser(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        Set<LinkedAccount> linkedAccounts = user.getLinkedAccounts();
        linkedAccounts.add(linkedAccount);
        user.setLinkedAccounts(linkedAccounts);

        this.userRepository.save(user);
        return linkedAccount;
    }

    public void removeLink(Long userId, AuthProvider authProvider) {
        User user = this.getUser(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        LinkedAccount linkedAccount = user.getLinkedAccounts().stream()
                .filter(acc -> acc.getAuthProvider().name().equalsIgnoreCase(authProvider.name()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " didn't link account with " + authProvider));

        user.getLinkedAccounts().remove(linkedAccount);
        this.userRepository.save(user);
        this.linkedAccountService.deleteLink(linkedAccount.getId());
    }

    public Optional<LinkedAccount> getLink(Long userId, AuthProvider authProvider) {
        User user = this.getUser(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        return user.getLinkedAccounts().stream()
                .filter(acc -> acc.getAuthProvider().name().equalsIgnoreCase(authProvider.name()))
                .findFirst();
    }

    public Set<LinkedAccount> getLinks(Long userId) {
        User user = this.getUser(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        return user.getLinkedAccounts();
    }

    public void updateLink(Long userId, AuthProvider authProvider, String providerUserEmail, String providerUserName) {
        User user = this.getUser(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        LinkedAccount linkedAccount = user.getLinkedAccounts().stream()
                .filter(acc -> acc.getAuthProvider().name().equalsIgnoreCase(authProvider.name()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " didn't link account with " + authProvider));

        this.linkedAccountService.updateLinkedAccount(linkedAccount.getId(), providerUserEmail, providerUserName);
    }

    public void setRoles(User user, Collection<Long> rolesId) {
        Set<Role> roles = rolesId.stream()
                .map(this::getRole)
                .collect(Collectors.toSet());
        user.setRoles(roles);
        this.userRepository.save(user);
    }

    public void addRole(User user, Long roleId) {
        Role role = getRole(roleId);
        user.getRoles().add(role);
        this.userRepository.save(user);
    }

    public void removeRole(User user, Long roleId) {
        Role role = getRole(roleId);
        user.getRoles().remove(role);
        this.userRepository.save(user);
    }

    public Set<User> getUsers() {
        return new HashSet<>(this.userRepository.findAll());
    }

    public Optional<User> getUser(String nameOrEmail) {
        return this.userRepository.findByNameOrEmail(nameOrEmail, nameOrEmail);
    }

    public Optional<User> getUser(long id) {
        return this.userRepository.findById(id);
    }

    private Role getRole(Long roleId) {
        return roleService.getRole(roleId).orElseThrow(() -> new NotFoundException("Role with id " + roleId + " not found"));
    }
}