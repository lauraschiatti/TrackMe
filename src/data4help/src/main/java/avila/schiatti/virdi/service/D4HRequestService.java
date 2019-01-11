package avila.schiatti.virdi.service;

import avila.schiatti.virdi.Data4HelpApp;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.exception.ValidationException;
import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.request.D4HRequestStatus;
import avila.schiatti.virdi.model.subscription.D4HQuery;
import avila.schiatti.virdi.model.subscription.Subscription;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.D4HUserRole;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import avila.schiatti.virdi.resource.D4HRequestResource;
import avila.schiatti.virdi.resource.SubscriptionResource;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.resource.APIManager;
import avila.schiatti.virdi.service.request.D4HReqRequest;
import avila.schiatti.virdi.service.response.D4HReqResponse;
import avila.schiatti.virdi.service.response.ResponseWrapper;
import avila.schiatti.virdi.utils.Validator;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Collection;

import static spark.Spark.*;

public class D4HRequestService extends Service {

    private D4HRequestResource requestResource;
    private SubscriptionResource subscriptionResource;
    private UserResource userResource;
    private APIManager apiManager;

    /**
     * Only for testing
     *
     * @param requestResource
     * @param subscriptionResource
     * @param userResource
     */
    public D4HRequestService(D4HRequestResource requestResource, SubscriptionResource subscriptionResource, UserResource userResource, APIManager apiManager) {
        this.requestResource = requestResource;
        this.subscriptionResource = subscriptionResource;
        this.userResource = userResource;
        this.apiManager = apiManager;
    }

    private D4HRequestService() {
        requestResource = D4HRequestResource.create();
        subscriptionResource = SubscriptionResource.create();
        userResource = UserResource.create();
        apiManager = APIManager.create();
    }

    public static D4HRequestService create() {
        return new D4HRequestService();
    }

    @Override
    public void setupWebEndpoints() {
        path("/requests", () -> {
            get("/", this::getAllRequests, jsonTransformer::toJson);

            post("/", this::createRequest, jsonTransformer::toJson);

            patch("/:id", this::updateRequestStatus, jsonTransformer::toJson);
        });
    }

    private ResponseWrapper<D4HReqResponse> updateRequestStatus(Request request, Response response) {
        D4HReqRequest body = jsonTransformer.fromJson(request.body(), D4HReqRequest.class);
        String userId = request.headers(Data4HelpApp.USER_ID);
        String rid = request.params(":id");

        D4HRequest req = requestResource.getById(rid);

        // if the request is not found
        if (req == null) {
            throw new TrackMeException(TrackMeError.NOT_VALID_REQUEST_ID);
        }
        // if the individual of the request does not belong to the logged in user
        if (!req.getIndividual().getId().toString().equals(userId)) {
            throw new TrackMeException(TrackMeError.NOT_VALID_USER);
        }

        if (D4HRequestStatus.APPROVED.equals(body.getStatus())) {
            requestResource.accept(req);
            // If the user accepts the request, we should create a subscription.
            Subscription subscription = createSubscription(req);
            subscriptionResource.add(subscription);
        } else {
            requestResource.reject(req);
            subscriptionResource.removeByRequest(req);
        }

        // sends a notification to the third party regarding the status of the request
        apiManager.sendNotification(req.getThirdParty(), req);

        return new ResponseWrapper<>(new D4HReqResponse(req));
    }

    private ResponseWrapper<Collection<D4HReqResponse>> getAllRequests(Request request, Response response) {
        D4HRequestStatus status = D4HRequestStatus.fromString(request.queryParams("status"));
        String userId = request.headers(Data4HelpApp.USER_ID);
        D4HUser user = userResource.getById(userId);

        if (user == null) {
            throw new TrackMeException(TrackMeError.NOT_VALID_USER);
        }

        Collection<D4HRequest> requests;

        if (D4HUserRole.INDIVIDUAL.equals(user.getRole())) {
            requests = requestResource.getByUserId(userId, status);
        } else {
            requests = requestResource.getByThirdPartyId(userId, status);
        }

        return new ResponseWrapper<>(transformCollection(requests));
    }

    private Collection<D4HReqResponse> transformCollection(Collection<D4HRequest> requests) {
        ArrayList<D4HReqResponse> list = new ArrayList<>();

        requests.forEach((r) -> {
            D4HReqResponse res = new D4HReqResponse(r);
            list.add(res);
        });

        return list;
    }

    private Subscription createSubscription(D4HRequest request) {
        Subscription subscription = new Subscription();
        D4HQuery filter = new D4HQuery();
        filter.setIndividual(request.getIndividual());

        subscription.setFilter(filter);
        subscription.setThirdParty(request.getThirdParty());
        subscription.setRequest(request);

        return subscription;
    }

    @Override
    public void setupApiEndpoints() {
        path("/requests", () -> {
            post("/", this::createRequest, jsonTransformer::toJson);

            delete("/:id", this::deleteRequest, jsonTransformer::toJson);
        });
    }

    private ResponseWrapper<String> deleteRequest(Request request, Response response) {
        String rid = request.params(":id");
        String secret = request.headers(Data4HelpApp.SECRET_KEY);
        ThirdParty tp = userResource.getThirdPartyBySecretKey(secret);

        D4HRequest req = requestResource.getById(rid);

        // if the request is not found
        if (req == null) {
            throw new TrackMeException(TrackMeError.NOT_VALID_REQUEST_ID);
        } else if (tp == null || !req.getThirdParty().getId().equals(tp.getId())) {
            // if the third party does not exit
            // if the individual of the request does not belong to the logged in user
            throw new TrackMeException(TrackMeError.NOT_VALID_SECRET_KEY);
        }

        requestResource.removeById(rid);
        subscriptionResource.removeByRequest(req);

        return new ResponseWrapper<>(rid);
    }

    private ResponseWrapper<D4HReqResponse> createRequest(Request request, Response response) {
        try {
            D4HReqRequest body = jsonTransformer.fromJson(request.body(), D4HReqRequest.class);
            String secret = request.headers(Data4HelpApp.SECRET_KEY);
            String thirdPartyId = request.headers(Data4HelpApp.USER_ID);

            ThirdParty tp;
            if(secret != null){
                // it is an api request
                tp = userResource.getThirdPartyBySecretKey(secret);
            }else {
                // it is a front-end request
                tp = (ThirdParty) userResource.getById(thirdPartyId);
            }

            Validator.isNullOrEmpty(body.getSsn(), "SSN");

            Individual i = userResource.getBySSN(body.getSsn());

            if(i == null){
                throw new TrackMeException(TrackMeError.NOT_VALID_USER);
            }

            D4HRequest d4HRequest = new D4HRequest();
            d4HRequest.setIndividual(i);
            d4HRequest.setThirdParty(tp);
            d4HRequest.setStatus(D4HRequestStatus.PENDING);

            requestResource.add(d4HRequest);

            D4HReqResponse res = new D4HReqResponse(d4HRequest);

            return new ResponseWrapper<>(res);

        } catch (ValidationException vex) {
            String msg = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), vex.getMessage());
            throw new TrackMeException(TrackMeError.VALIDATION_ERROR, msg);
        }
    }
}
