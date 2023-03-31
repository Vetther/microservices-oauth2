package pl.owolny.userservice.linkedaccount;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.owolny.userservice.authprovider.AuthProvider;
import pl.owolny.userservice.exception.NotFoundException;

@Service
@AllArgsConstructor
public class LinkedAccountService {

    private final LinkedAccountRepository linkedAccountRepository;

    public LinkedAccount createLink(AuthProvider authProvider, String providerUserId, String providerUserEmail, String providerUserName) {
        return this.linkedAccountRepository.save(new LinkedAccount(null, authProvider, providerUserId, providerUserEmail, providerUserName));
    }

    public void deleteLink(Long linkedAccountId) {
        this.linkedAccountRepository.deleteById(linkedAccountId);
    }

    public void updateLinkedAccount(Long linkedAccountId, String providerUserEmail, String providerUserName) {
        LinkedAccount linkedAccount = this.linkedAccountRepository.findById(linkedAccountId).orElseThrow(() -> new NotFoundException("Linked account with id " + linkedAccountId + " not found"));
        linkedAccount.setProviderUserEmail(providerUserEmail);
        linkedAccount.setProviderUserName(providerUserName);
        this.linkedAccountRepository.save(linkedAccount);
    }
}
