package avila.schiatti.virdi.service;

import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.exception.ValidationException;
import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.resource.DataResource;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.request.DataRequest;
import avila.schiatti.virdi.utils.Validator;
import org.bson.types.ObjectId;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class DataService extends Service {

    private DataResource dataResource;
    private UserResource userResource;

    /**
     * Only for testing
     *
     * @param dataResource
     */
    public DataService(DataResource dataResource, UserResource userResource) {
        this.dataResource = dataResource;
        this.userResource = userResource;
    }

    private DataService() {
        dataResource = DataResource.create();
        userResource = UserResource.create();
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

            // asynchronous update of the individual data.
            (new Thread(() -> {
                Individual individual = userResource.getBySSN(body.getSsn());

                if(individual != null) {
                    Data data = dataResource.getByIndividual(individual);
                    data.setHealthStatus(body.getHealthStatus());
                    data.setLocation(body.getLocation());

                    dataResource.update(data);
                }
            })).start();

            res.status(HttpStatus.OK_200);

        } catch (ValidationException vex) {
            String msg = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), vex.getMessage());
            throw new TrackMeException(TrackMeError.VALIDATION_ERROR, msg);
        }

        return "";
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
