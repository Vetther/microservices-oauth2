package pl.owolny.authenticationserver.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyResponse {

    private String key;
    private String providerUserId;
    private String providerUserName;
    private String providerUserEmail;
    private String providerUserImageUrl;

    public KeyResponse(String key, String providerUserId, String providerUserName, String providerUserEmail, String providerUserImageUrl) {
        this.key = key;
        this.providerUserId = providerUserId;
        this.providerUserName = providerUserName;
        this.providerUserEmail = providerUserEmail;
        this.providerUserImageUrl = providerUserImageUrl;
    }

    public KeyResponse() {

    }
}