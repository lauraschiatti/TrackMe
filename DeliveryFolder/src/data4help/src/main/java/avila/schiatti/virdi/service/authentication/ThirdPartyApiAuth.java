package avila.schiatti.virdi.service.authentication;

public class ThirdPartyApiAuth {
    private String secretKey;
    private String appId;

    public ThirdPartyApiAuth(String secretKey, String appId) {
        this.secretKey = secretKey;
        this.appId = appId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getAppId() {
        return appId;
    }
}
