package avila.schiatti.virdi.service;

import avila.schiatti.virdi.resource.APIManager;
import spark.Request;
import spark.Response;

import static spark.Spark.post;

public class DataService extends Service {

    private APIManager apiManager;

    private DataService() {
        apiManager = APIManager.create();
    }

    public static DataService create() {
        return new DataService();
    }

    @Override
    public void setupApiEndpoints() {
        post("/data", this::updateData, jsonTransformer::toJson);

        post("/notification", this::getNotification, jsonTransformer::toJson);
    }

    private String getNotification(Request request, Response response) {
        return null;
    }

    private String updateData(Request req, Response res) {
        return "";
    }
}
