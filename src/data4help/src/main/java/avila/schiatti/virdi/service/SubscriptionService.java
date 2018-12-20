package avila.schiatti.virdi.service;

import avila.schiatti.virdi.resource.APIManager;
import avila.schiatti.virdi.resource.D4HRequestResource;
import avila.schiatti.virdi.resource.SubscriptionResource;
import avila.schiatti.virdi.resource.UserResource;
import spark.Spark;

import static spark.Spark.*;

public class SubscriptionService extends Service {

    private D4HRequestResource requestResource;
    private SubscriptionResource subscriptionResource;
    private UserResource userResource;
    private APIManager apiManager;

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
        apiManager = APIManager.create();
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

//    SubscriptionResponse createSubscription(Spark.Request req, Spark.Response res)
//Collection<Subscription> getAllSubscriptions(Spark.Request req,
//                                               Spark.Response res)
//void removeSubscription(Spark.Request req, Spark.Response res)
}
