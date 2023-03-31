package pl.owolny.userservice.linkedaccount;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.owolny.userservice.authprovider.AuthProvider;

import java.util.Optional;

public interface LinkedAccountRepository extends JpaRepository<LinkedAccount, Long> {

}
