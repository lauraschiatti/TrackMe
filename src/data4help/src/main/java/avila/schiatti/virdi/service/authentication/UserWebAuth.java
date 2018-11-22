package avila.schiatti.virdi.service.authentication;

public class UserWebAuth {
    private String userId;
    private String accessToken;

    public UserWebAuth(String userId, String accessToken) {
        this.userId = userId;
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
