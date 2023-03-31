package pl.owolny.userservice.user;

import jakarta.persistence.*;
import lombok.*;
import pl.owolny.userservice.linkedaccount.LinkedAccount;
import pl.owolny.userservice.role.Role;

import java.util.*;

import static jakarta.persistence.FetchType.EAGER;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}