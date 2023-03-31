package pl.owolny.userservice.role;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(String name) {
        return this.roleRepository.save(new Role(null, name));
    }

    public Set<Role> getRoles() {
        return new HashSet<>(this.roleRepository.findAll());
    }

    public Optional<Role> getRole(String name) {
        return this.roleRepository.findByName(name);
    }

    public Optional<Role> getRole(Long id) {
        return this.roleRepository.findById(id);
    }
}