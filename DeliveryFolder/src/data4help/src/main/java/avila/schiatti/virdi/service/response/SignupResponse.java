package avila.schiatti.virdi.service.response;

import avila.schiatti.virdi.model.user.D4HUserRole;

public class SignupResponse {
    private String userId;
    private String accessToken;
    private D4HUserRole role;

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

    public D4HUserRole getRole() {
        return role;
    }

    public void setRole(D4HUserRole role) {
        this.role = role;
    }
}
