package pl.owolny.oauth2service.redis.provideruserdata;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash(timeToLive = 5 * 60)
public class ProviderUserData {

    @Id
    private String key;
    private String providerUserId;
    private String providerUserName;
    private String providerUserEmail;
    private String providerUserImageUrl;

    public ProviderUserData(String key, String providerUserId, String providerUserName, String providerUserEmail, String providerUserImageUrl) {
        this.key = key;
        this.providerUserId = providerUserId;
        this.providerUserName = providerUserName;
        this.providerUserEmail = providerUserEmail;
        this.providerUserImageUrl = providerUserImageUrl;
    }

    public ProviderUserData() {

    }
}