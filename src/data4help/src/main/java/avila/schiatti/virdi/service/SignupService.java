package avila.schiatti.virdi.service;

import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.request.IndividualSignupRequest;
import avila.schiatti.virdi.service.response.SignupResponse;
import avila.schiatti.virdi.utils.JsonUtil;
import spark.Request;
import spark.Response;

public class SignupService implements Service {

    private static SignupService _instance;
    private UserResource userResource = UserResource.getInstance();

    public static SignupService getInstance(){
        if(_instance == null){
            _instance = new SignupService();
        }
        return _instance;
    }

    private SignupResponse signupIndividual(Request req, Response res){
        IndividualSignupRequest body = JsonUtil.fromJson(req.body(), IndividualSignupRequest.class);
        userResource.add(new Individual());
        return null;
    }

    private SignupResponse signupThirdParty(Request req, Response res){
        return null;
    }

    @Override
    public void setupWebEndpoints() {

    }

    @Override
    public void setupApiEndpoints() {

    }

    @Override
    public void setupExceptionHandlers() {

    }
}
