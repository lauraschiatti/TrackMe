package avila.schiatti.virdi.service;

import avila.schiatti.virdi.Data4HelpApp;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.model.data.BloodType;
import avila.schiatti.virdi.model.data.Gender;
import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.subscription.D4HQuery;
import avila.schiatti.virdi.model.subscription.Subscription;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.D4HUserRole;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import avila.schiatti.virdi.resource.APIManager;
import avila.schiatti.virdi.resource.D4HRequestResource;
import avila.schiatti.virdi.resource.SubscriptionResource;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.request.SubscriptionRequest;
import avila.schiatti.virdi.service.response.ResponseWrapper;
import avila.schiatti.virdi.service.response.SubscriptionResponse;
import avila.schiatti.virdi.utils.Validator;
import spark.Request;
import spark.Response;

import java.util.Collection;

import static spark.Spark.*;

public class SubscriptionService extends Service {

    private D4HRequestResource requestResource;
    private SubscriptionResource subscriptionResource;
    private UserResource userResource;

    /**
     * Only for testing
     * @param requestResource
     * @param subscriptionResource
     * @param userResource
     */
    public SubscriptionService(D4HRequestResource requestResource, SubscriptionResource subscriptionResource, UserResource userResource) {
        this.requestResource = requestResource;
        this.subscriptionResource = subscriptionResource;
        this.userResource = userResource;
    }

    private SubscriptionService() {
        requestResource = D4HRequestResource.create();
        subscriptionResource = SubscriptionResource.create();
        userResource = UserResource.create();
    }

    public static SubscriptionService create(){
        return new SubscriptionService();
    }


    @Override
    public void setupWebEndpoints() {
        path("/subscriptions", ()->{
            post("/", this::createSubscription, jsonTransformer::toJson);

            get("/", this::getAllSubscriptions, jsonTransformer::toJson);

            delete("/:id", this::removeSubscription, jsonTransformer::toJson);
        });
    }

    private ResponseWrapper<SubscriptionResponse> createSubscription(Request req, Response res){
        SubscriptionRequest body = jsonTransformer.fromJson(req.body(), SubscriptionRequest.class);
        String thirdPartyId = req.headers(Data4HelpApp.USER_ID);

        ThirdParty tp = (ThirdParty) userResource.getById(thirdPartyId);

        if(body == null || body.getFilter() == null){
            throw new TrackMeException(TrackMeError.NULL_OR_NOT_VALID_FILTER);
        }

        Subscription subscription = new Subscription();
        subscription.setThirdParty(tp);
        subscription.setTimeSpan(body.getTimeSpan());
        subscription.setFilter(createFilter(body.getFilter()));

        if(subscription.getFilter().getIndividual() != null && subscription.getFilter().getIndividual().getSsn() != null){
            String ssn = subscription.getFilter()
                    .getIndividual()
                    .getSsn();
            Individual i = userResource.getBySSN(ssn);

            // should create a subscription only when the request was accepted.
            D4HRequest request = requestResource.checkApprovedRequest(i.getId(), tp.getId());

            D4HQuery filter = new D4HQuery();
            filter.setIndividual(i);
            subscription.setFilter(filter);
            subscription.setRequest(request);
        } else{
            subscription.getFilter().setIndividual(null);
        }

        subscriptionResource.add(subscription);

        SubscriptionResponse response = new SubscriptionResponse();
        response.setFilter(subscription.getFilter());
        response.setSubscriptionId(subscription.getId().toString());

        return new ResponseWrapper<>(response);
    }

    private D4HQuery createFilter(D4HQuery filter) {
        D4HQuery query = new D4HQuery();
        query.setCity(Validator.isNullOrEmpty(filter.getCity()) ? null : filter.getCity());
        query.setCountry(Validator.isNullOrEmpty(filter.getCountry()) ? null : filter.getCountry());
        query.setProvince(Validator.isNullOrEmpty(filter.getProvince()) ? null : filter.getProvince());
        query.setMinAge(filter.getMinAge() <= 0 ? null : filter.getMinAge());
        query.setMaxAge(filter.getMaxAge() <= 0  || filter.getMaxAge() < filter.getMinAge() ? null : filter.getMaxAge());
        query.setBloodType(filter.getBloodType());
        query.setGender(filter.getGender());
        return query;
    }

    private ResponseWrapper<String> removeSubscription(Request req, Response res){
        String id = req.params(":id");
        String thirdPartyId = req.headers(Data4HelpApp.USER_ID);

        Subscription subscription = subscriptionResource.getByOwnerAndId(thirdPartyId, id);
        if(subscription == null){
            throw new TrackMeException(TrackMeError.NOT_VALID_SUBSCRIPTION);
        }

        subscriptionResource.remove(subscription);

        return new ResponseWrapper<>(id);
    }

    private ResponseWrapper<Collection<Subscription>> getAllSubscriptions(Request req, Response res){
        String userId = req.headers(Data4HelpApp.USER_ID);
        D4HUser user = userResource.getById(userId);

        Collection<Subscription> subscriptions;

        if(D4HUserRole.INDIVIDUAL.equals(user.getRole())){
            subscriptions = subscriptionResource.getAllByIndividual((Individual) user);
        }else{
            subscriptions = subscriptionResource.getAllByOwner((ThirdParty) user);
        }

        if(subscriptions != null && !subscriptions.isEmpty()){
            subscriptions.forEach((s) -> {
                s.getFilter().setIndividual(cloneIndividual(s.getFilter().getIndividual()));
                s.setThirdParty(cloneThirdParty(s.getThirdParty()));
                // remove request from response
                s.setRequest(null);
            });
        }

        return new ResponseWrapper<>(subscriptions);
    }

    private ThirdParty cloneThirdParty(ThirdParty thirdParty) {
        ThirdParty tp = new ThirdParty();
        tp.setName(thirdParty.getName());
        tp.setPhone(thirdParty.getPhone());

        return tp;
    }

    private Individual cloneIndividual(Individual individual) {
        if(individual == null){
            return null;
        }

        Individual i = new Individual();
        i.setName(individual.getName());
        i.setSsn(individual.getSsn());

        return i;
    }
}
