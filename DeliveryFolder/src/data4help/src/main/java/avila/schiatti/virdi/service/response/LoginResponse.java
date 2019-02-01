package avila.schiatti.virdi.service.response;

import avila.schiatti.virdi.model.user.D4HUserRole;

public class LoginResponse {
    private String userId;
    private String accessToken;
    private D4HUserRole role;

    public LoginResponse(String userId, String accessToken, D4HUserRole role) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public D4HUserRole getRole() {
        return role;
    }
}
