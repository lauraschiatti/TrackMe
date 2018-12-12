package avila.schiatti.virdi.service.response;

import avila.schiatti.virdi.model.user.UserRole;

public class SignupResponse {
    private String userId;
    private String accessToken;
    private UserRole role;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
