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
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class LoginService implements Service {
    private static LoginService _instance = null;
    private UserResource userResource;
    private AuthenticationManager authManager;

    private LoginService() {
        authManager = AuthenticationManager.getInstance();
        userResource = UserResource.getInstance();
    }

    public static LoginService getInstance(){
        if(_instance == null){
            _instance = new LoginService();
        }
        return _instance;
    }

    private D4HUser validateCredentials(String email, String password){
        String pass = authManager.hashPassword(password);
        return userResource.getByEmailAndPass(email, pass);
    }

    private LoginResponse login(Request request, Response response){
        LoginRequest body = JsonUtil.fromJson(request.body(), LoginRequest.class);
        D4HUser user = this.validateCredentials(body.getEmail(), body.getPassword());

        if(user != null){
            UserWebAuth uAuth = authManager.setUserAccessToken(user);
            return new LoginResponse(uAuth.getUserId(), uAuth.getAccessToken());
        }

        throw new TrackMeException(TrackMeError.NOT_VALID_EMAIL_OR_PASSWORD);
    }

    private Void logout(Request request, Response response) {
        LogoutRequest body = JsonUtil.fromJson(request.body(), LogoutRequest.class);

        authManager.deleteAccessToken(body.getAccessToken());

        response.status(HttpStatus.OK_200);
        return null;
    }

    @Override
    public void setupWebEndpoints() {
        post("/login", this::login, JsonUtil::toJson);

        post("/logout", this::logout);
    }

    @Override
    public void setupApiEndpoints() {

    }

    @Override
    public void setupExceptionHandlers() {

    }
}
