package pl.owolny.oauth2service.key;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Key {

    private String key;
    private String providerUserId;
    private String providerUserName;
    private String providerUserEmail;
    private String providerUserImageUrl;

    public Key(String key, String providerUserId, String providerUserName, String providerUserEmail, String providerUserImageUrl) {
        this.key = key;
        this.providerUserId = providerUserId;
        this.providerUserName = providerUserName;
        this.providerUserEmail = providerUserEmail;
        this.providerUserImageUrl = providerUserImageUrl;
    }

    public Key() {

    }
}