package pl.owolny.userservice.linkedaccount;

import jakarta.persistence.*;
import lombok.*;
import pl.owolny.userservice.authprovider.AuthProvider;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PACKAGE)
public class LinkedAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private AuthProvider authProvider;

    private String providerUserId;

    private String providerUserEmail;

    private String providerUserName;
}


