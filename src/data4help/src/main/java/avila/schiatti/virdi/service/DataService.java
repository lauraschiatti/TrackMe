package avila.schiatti.virdi.service;

import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.exception.ValidationException;
import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.subscription.Subscription;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.resource.APIManager;
import avila.schiatti.virdi.resource.DataResource;
import avila.schiatti.virdi.resource.SubscriptionResource;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.request.DataRequest;
import avila.schiatti.virdi.utils.Validator;
import org.bson.types.ObjectId;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import java.util.Collection;

import static spark.Spark.*;

public class DataService extends Service {

    private DataResource dataResource;
    private UserResource userResource;
    private SubscriptionResource subscriptionResource;
    private APIManager apiManager;

    /**
     * Only for testing
     *
     * @param dataResource
     */
    public DataService(DataResource dataResource, UserResource userResource, SubscriptionResource subscriptionResource, APIManager apiManager) {
        this.dataResource = dataResource;
        this.userResource = userResource;
        this.subscriptionResource = subscriptionResource;
        this.apiManager = apiManager;
    }

    private DataService() {
        dataResource = DataResource.create();
        userResource = UserResource.create();
        subscriptionResource = SubscriptionResource.create();
        apiManager = APIManager.create();
    }

    public static DataService create() {
        return new DataService();
    }

//    @Override
    public void setupInternalEndpoints() {
        post("/data", this::updateData, jsonTransformer::toJson);
    }

    private String updateData(Request req, Response res) {
        try {
            DataRequest body = jsonTransformer.fromJson(req.body(), DataRequest.class);

            Validator.isNullOrEmpty(body.getSsn(), "SSN");

            Individual individual = userResource.getBySSN(body.getSsn());

            if(individual == null) {
                throw new TrackMeException(TrackMeError.NOT_VALID_USER);
            }

            Data data = dataResource.getByIndividual(individual);
            data.setHealthStatus(body.getHealthStatus());
            data.setLocation(body.getLocation());

            dataResource.update(data);
            data.setIndividual(null);

            // asynchronously notify all the third parties
            (new Thread(()->{
                Collection<Subscription> subscriptions = subscriptionResource.getAllByIndividual(individual);
                subscriptions.forEach( (s) -> apiManager.sendData(s.getThirdParty(), individual.getSsn(), data) );
            })).start();

            res.status(HttpStatus.OK_200);
            return "";

        } catch (ValidationException vex) {
            String msg = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), vex.getMessage());
            throw new TrackMeException(TrackMeError.VALIDATION_ERROR, msg);
        }
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
