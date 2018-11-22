package avila.schiatti.virdi.service.request;

public class LogoutRequest {
    private String accessToken;

    public LogoutRequest(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
