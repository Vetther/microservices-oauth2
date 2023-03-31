package pl.owolny.userservice.linkedaccount;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class LinkedAccountDTOMapper implements Function<LinkedAccount, LinkedAccountDTO> {

    @Override
    public LinkedAccountDTO apply(LinkedAccount linkedAccount) {
        return new LinkedAccountDTO(
                linkedAccount.getId(),
                linkedAccount.getAuthProvider(),
                linkedAccount.getProviderUserId(),
                linkedAccount.getProviderUserEmail(),
                linkedAccount.getProviderUserName()
        );
    }
}