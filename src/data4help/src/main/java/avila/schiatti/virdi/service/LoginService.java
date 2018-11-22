package avila.schiatti.virdi.service;

import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.service.request.LoginRequest;
import avila.schiatti.virdi.service.request.LogoutRequest;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.response.LoginResponse;
import avila.schiatti.virdi.utils.JsonUtil;
import avila.schiatti.virdi.service.authentication.*;
import org.eclipse.jetty.http.HttpStatus;

import static spark.Spark.*;

public class LoginService implements Service {
    private static LoginService _instance = null;
    private UserResource userResource;
    private AuthenticationService authService;

    private LoginService() {
        authService = AuthenticationService.getInstance();
        userResource = UserResource.getInstance();
    }

    public static LoginService getInstance(){
        if(_instance == null){
            _instance = new LoginService();
        }
        return _instance;
    }

    private D4HUser validateCredentials(String email, String password){
        return userResource.getByEmailAndPass(email, password);
    }

    @Override
    public void setupWebEndpoints() {
        post("/login", (request, response) -> {
            LoginRequest body = JsonUtil.fromJson(request.body(), LoginRequest.class);
            D4HUser user = this.validateCredentials(body.getEmail(), body.getPassword());

            if(user != null){
                UserWebAuth uAuth = authService.setUserAccessToken(user);
                return new LoginResponse(uAuth.getUserId(), uAuth.getAccessToken());
            }

            throw new TrackMeException(TrackMeError.NOT_VALID_EMAIL_OR_PASSWORD);
        }, JsonUtil::toJson);

        post("/logout", (request, response) -> {
            LogoutRequest body = JsonUtil.fromJson(request.body(), LogoutRequest.class);

            authService.deleteAccessToken(body.getAccessToken());

            response.status(HttpStatus.OK_200);
            return "";
        });
    }

    @Override
    public void setupApiEndpoints() {

    }

    @Override
    public void setupExceptionHandlers() {

    }
}
