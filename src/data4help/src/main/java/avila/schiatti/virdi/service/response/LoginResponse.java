package avila.schiatti.virdi.service.response;

public class LoginResponse {
    private String userId;
    private String accessToken;

    public LoginResponse(String userId, String accessToken) {
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
