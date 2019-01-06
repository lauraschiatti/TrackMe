package avila.schiatti.virdi.service;

import avila.schiatti.virdi.Data4HelpApp;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.model.user.*;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.request.SubscriptionRequest;
import avila.schiatti.virdi.service.response.ResponseWrapper;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class UserService extends Service {

    private UserResource userResource;

    /**
     * Only for testing
     * @param userResource
     */
    public UserService(UserResource userResource) {
        this.userResource = userResource;
    }

    private UserService(){
        userResource = UserResource.create();
    }

    public static UserService create(){
        return new UserService();
    }

    @Override
    public void setupWebEndpoints() {
        path("/me", ()->{
            get("/", this::getProfileInfo, jsonTransformer::toJson);

            patch("/config", this::updateThirdPartyConfig, jsonTransformer::toJson);
        });
    }

    private ResponseWrapper<TPConfiguration> updateThirdPartyConfig(Request req, Response res) {
        String userId = req.headers(Data4HelpApp.USER_ID);
        ThirdParty user = (ThirdParty) userResource.getById(userId);

        TPConfiguration configuration = jsonTransformer.fromJson(req.body(), TPConfiguration.class);

        if(!D4HUserRole.THIRD_PARTY.equals(user.getRole())){
            throw new TrackMeException(TrackMeError.UNAUTHORIZED_USER);
        }

        user.setConfig(configuration);
        userResource.update(user);

        return new ResponseWrapper<>(configuration);
    }

    private ResponseWrapper<D4HUser> getProfileInfo(Request req, Response res) {
        String userId = req.headers(Data4HelpApp.USER_ID);

        D4HUser user = userResource.getById(userId);

        user.setId(null);
        user.setPassword(null);

        return new ResponseWrapper<>(user);
    }
}
