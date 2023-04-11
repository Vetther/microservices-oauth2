package pl.owolny.userservice.user;

import org.springframework.stereotype.Service;
import pl.owolny.userservice.role.Role;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserDTOMapper implements Function<User, UserDTO> {

    @Override
    public UserDTO apply(User user) {
        return new UserDTO(
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getImageUrl(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList())
        );
    }
}