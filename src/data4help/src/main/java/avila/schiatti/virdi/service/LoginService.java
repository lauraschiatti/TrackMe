package avila.schiatti.virdi.service;

import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.service.request.LoginRequest;
import avila.schiatti.virdi.service.request.LogoutRequest;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.response.LoginResponse;
import avila.schiatti.virdi.service.authentication.*;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import sun.rmi.runtime.Log;

import static spark.Spark.*;

public class LoginService extends Service {
    private UserResource userResource;
    private AuthenticationManager authManager;

    private LoginService() {
        authManager = AuthenticationManager.getInstance();
        userResource = UserResource.create();
    }

    /**
     * Only for testing.
     * @param authManager
     * @param userResource
     */
    public LoginService(AuthenticationManager authManager, UserResource userResource){
        this.authManager = authManager;
        this.userResource = userResource;
    }

    public static LoginService create(){
        return new LoginService();
    }

    private D4HUser validateCredentials(String email, String password){
        String pass = AuthenticationManager.hashPassword(password);
        return userResource.getByEmailAndPass(email, pass);
    }

    private LoginResponse login(Request request, Response response){
        LoginRequest body = jsonTransformer.fromJson(request.body(), LoginRequest.class);
        D4HUser user = this.validateCredentials(body.getEmail(), body.getPassword());

        if(user != null){
            UserWebAuth uAuth = authManager.setUserAccessToken(user);
            return new LoginResponse(uAuth.getUserId(), uAuth.getAccessToken());
        }

        throw new TrackMeException(TrackMeError.NOT_VALID_EMAIL_OR_PASSWORD);
    }

    private String logout(Request request, Response response) {
        LogoutRequest body = jsonTransformer.fromJson(request.body(), LogoutRequest.class);

        authManager.deleteAccessToken(body.getAccessToken());

        response.status(HttpStatus.OK_200);
        return "";
    }

    @Override
    public void setupWebEndpoints() {
        post("/login", this::login, jsonTransformer::toJson);

        post("/logout", this::logout);
    }

    @Override
    public void setupApiEndpoints() {

    }

    @Override
    public void setupExceptionHandlers() {

    }
}
