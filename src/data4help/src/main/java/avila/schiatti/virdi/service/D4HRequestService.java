package avila.schiatti.virdi.service;

import avila.schiatti.virdi.Data4HelpApp;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.exception.ValidationException;
import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.request.D4HRequestStatus;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import avila.schiatti.virdi.resource.D4HRequestResource;
import avila.schiatti.virdi.resource.SubscriptionResource;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.request.D4HRRequest;
import avila.schiatti.virdi.service.response.D4HRResponse;
import avila.schiatti.virdi.utils.Validator;
import spark.Request;
import spark.Response;

import java.util.Collection;

import static spark.Spark.get;
import static spark.Spark.post;

public class D4HRequestService extends Service {

    private D4HRequestResource requestResource;
    private SubscriptionResource subscriptionResource;
    private UserResource userResource;

    /**
     * Only for testing
     * @param requestResource
     * @param subscriptionResource
     * @param userResource
     */
    public D4HRequestService(D4HRequestResource requestResource, SubscriptionResource subscriptionResource, UserResource userResource) {
        this.requestResource = requestResource;
        this.subscriptionResource = subscriptionResource;
        this.userResource = userResource;
    }

    private D4HRequestService() {
        requestResource = D4HRequestResource.create();
        subscriptionResource = SubscriptionResource.create();
        userResource = UserResource.create();
    }

    public static D4HRequestService create(){
        return new D4HRequestService();
    }

    @Override
    public void setupWebEndpoints() {
        get("/request", this::getAllRequests, jsonTransformer::toJson);
    }

    private Collection<D4HRequest> getAllRequests(Request request, Response response) {
        D4HRequestStatus status = D4HRequestStatus.fromString(request.queryParams("status"));
        String userId = request.headers(Data4HelpApp.USER_ID);

        if(status == null){
            return requestResource.getByUserId(userId);
        }

        return requestResource.getByUserId(userId, status);
    }

    @Override
    public void setupApiEndpoints() {
        post("/request", this::createRequest, jsonTransformer::toJson);
    }

    private D4HRResponse createRequest(Request request, Response response) {
        try {
            D4HRRequest body = jsonTransformer.fromJson(request.body(), D4HRRequest.class);
            String secret = request.headers(Data4HelpApp.SECRET_KEY);

            Validator.isNullOrEmpty(body.getSsn(), "SSN");

            ThirdParty tp = userResource.getThirdPartyBySecretKey(secret);
            Individual i = userResource.getBySSN(body.getSsn());

            D4HRequest d4HRequest = new D4HRequest();
            d4HRequest.setIndividual(i);
            d4HRequest.setThirdParty(tp);
            d4HRequest.setStatus(D4HRequestStatus.PENDING);

            requestResource.add(d4HRequest);

            D4HRResponse res = new D4HRResponse();
            res.setId(d4HRequest.getId().toString());
            res.setSsn(i.getSsn());

            return res;

        }catch(ValidationException vex){
            String msg = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), vex.getMessage());
            throw new TrackMeException(TrackMeError.VALIDATION_ERROR, msg);
        }
    }

    @Override
    public void setupExceptionHandlers() {

    }
}
