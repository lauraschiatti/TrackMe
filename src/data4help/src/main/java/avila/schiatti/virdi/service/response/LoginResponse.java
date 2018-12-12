package avila.schiatti.virdi.service.response;

import avila.schiatti.virdi.model.user.UserRole;

public class LoginResponse {
    private String userId;
    private String accessToken;
    private UserRole role;

    public LoginResponse(String userId, String accessToken, UserRole role) {
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

    public UserRole getRole() {
        return role;
    }
}
