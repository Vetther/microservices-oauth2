package pl.owolny.userservice.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.owolny.userservice.authprovider.AuthProvider;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where upper(u.name) = upper(?1) or upper(u.email) = upper(?2)")
    Optional<User> findByNameOrEmail(String name, String email);

    Optional<User> findByLinkedAccounts_AuthProvider(AuthProvider authProvider);

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    Boolean existsByEmail(String email);
}