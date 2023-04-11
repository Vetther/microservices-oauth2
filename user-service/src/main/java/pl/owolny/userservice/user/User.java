package pl.owolny.userservice.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.owolny.userservice.linkedaccount.LinkedAccount;
import pl.owolny.userservice.role.Role;

import java.util.*;

import static jakarta.persistence.FetchType.EAGER;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String username;

    private String password;

    @Column(nullable = false)
    private String email;

    private String imageUrl;

    @OneToMany(fetch = EAGER)
    private Set<LinkedAccount> linkedAccounts = new HashSet<>();

    @ManyToMany(fetch = EAGER)
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}