package avila.schiatti.virdi.service;

import avila.schiatti.virdi.Data4HelpApp;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.exception.ValidationException;
import avila.schiatti.virdi.model.request.D4HRequestStatus;
import avila.schiatti.virdi.model.user.*;
import avila.schiatti.virdi.resource.D4HRequestResource;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.response.ResponseWrapper;
import avila.schiatti.virdi.service.response.D4HUserResponse;
import avila.schiatti.virdi.utils.Validator;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class UserService extends Service {

    private UserResource userResource;
    private D4HRequestResource requestResource;

    /**
     * Only for testing
     * @param userResource
     */
    public UserService(UserResource userResource, D4HRequestResource requestResource) {
        this.userResource = userResource;
        this.requestResource = requestResource;
    }

    private UserService(){
        userResource = UserResource.create();
        requestResource = D4HRequestResource.create();
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

        try {
            validateConfiguration(configuration);

            user.setConfig(configuration);
            userResource.update(user);

        }catch(ValidationException vex){
            throw new TrackMeException(TrackMeError.VALIDATION_ERROR, vex.getMessage());
        }

        return new ResponseWrapper<>(configuration);
    }

    private ResponseWrapper<D4HUserResponse> getProfileInfo(Request req, Response res) {
        String userId = req.headers(Data4HelpApp.USER_ID);

        D4HUser user = userResource.getById(userId);

        Long approvedReqs = requestResource.countByUserId(user.getId(), D4HRequestStatus.APPROVED);
        Long pendingReqs = requestResource.countByUserId(user.getId(), D4HRequestStatus.PENDING);
        Long rejectedReqs = requestResource.countByUserId(user.getId(), D4HRequestStatus.REJECTED);


        D4HUserResponse response;
        if(D4HUserRole.INDIVIDUAL.equals(user.getRole())){
            response = D4HUserResponse.fromIndividual((Individual) user);
        }else{
            response = D4HUserResponse.fromThirdParty((ThirdParty) user);
        }
        response.setApprovedRequests(approvedReqs);
        response.setPendingRequests(pendingReqs);
        response.setRejectedRequests(rejectedReqs);

        return new ResponseWrapper<>(response);
    }

    private void validateConfiguration(TPConfiguration config){
        if(config != null){
            Validator.validateURL(config.getNotificationUrl());
            Validator.validateURL(config.getIndividualPushUrl());
            Validator.validateURL(config.getBulkPushUrl());
        }
    }
}
