package avila.schiatti.virdi.service;

import avila.schiatti.virdi.Data4HelpApp;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import avila.schiatti.virdi.service.authentication.UserWebAuth;
import avila.schiatti.virdi.service.request.LoginRequest;
import avila.schiatti.virdi.service.response.LoginResponse;
import avila.schiatti.virdi.utils.Validator;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import static spark.Spark.post;
import static spark.Spark.head;

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
            return new LoginResponse(uAuth.getUserId(), uAuth.getAccessToken(), user.getRole());
        }

        throw new TrackMeException(TrackMeError.NOT_VALID_EMAIL_OR_PASSWORD);
    }

    private String logout(Request request, Response response) {
        String accessToken = request.headers(Data4HelpApp.ACCESS_TOKEN);

        authManager.deleteAccessToken(accessToken);

        response.status(HttpStatus.OK_200);
        return "";
    }

    private String isValidToken(Request req, Response res){
        String userId = req.headers(Data4HelpApp.USER_ID);
        String token = req.headers(Data4HelpApp.ACCESS_TOKEN);

        authManager.validateAccessToken(userId, token);

        return "";
    }

    @Override
    public void setupWebEndpoints() {
        post("/login", this::login, jsonTransformer::toJson);

        post("/logout", this::logout);

        head("/login", this::isValidToken, jsonTransformer::toJson);
    }
}
