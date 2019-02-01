package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.user.ThirdParty;
import avila.schiatti.virdi.resource.api.BulkDataRequest;
import avila.schiatti.virdi.resource.api.IndividualDataRequest;
import avila.schiatti.virdi.resource.api.NotificationRequest;
import avila.schiatti.virdi.utils.JSONObjectMapper;
import avila.schiatti.virdi.utils.Validator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unirest.Unirest;

import java.util.Collection;

public class APIManager {
    private static Logger logger = LoggerFactory.getLogger(APIManager.class);

    private static final String APPLICATION_JSON = "application/json";
    private static final Gson jsonTransformer = new GsonBuilder().create();

    private APIManager() {
        Unirest.config().setObjectMapper(new JSONObjectMapper());
    }

    public static APIManager create(){
        return new APIManager();
    }

    public void sendNotification(ThirdParty tp, D4HRequest request) {
        if (tp != null && tp.getConfig() != null && Validator.isURL(tp.getConfig().getNotificationUrl())) {
            String url = tp.getConfig().getNotificationUrl();

            this.trace("Notification", tp, tp.getConfig().getNotificationUrl(), request);

            NotificationRequest notification = new NotificationRequest(request);
            this.post(url, notification);
        }
    }

    public void sendData(ThirdParty tp, String ssn, Data data){
        if(tp != null && tp.getConfig() != null && Validator.isURL(tp.getConfig().getIndividualPushUrl())){
            String url = tp.getConfig().getIndividualPushUrl();

            this.trace("Individual Data", tp, tp.getConfig().getNotificationUrl(), data);

            IndividualDataRequest request = new IndividualDataRequest(ssn, data);
            this.post(url, request);
        }
    }

    public void sendData(ThirdParty tp, Collection<Data> bulkData){
        if(tp != null && tp.getConfig() != null && Validator.isURL(tp.getConfig().getBulkPushUrl())){
            String url = tp.getConfig().getBulkPushUrl();

            this.trace("Bulk Data", tp, tp.getConfig().getNotificationUrl(), bulkData);

            BulkDataRequest data = new BulkDataRequest(bulkData);
            this.post(url, data);
        }
    }

    private void post(String url, Object object) {
        Unirest.post(url)
                .header("content-type", APPLICATION_JSON)
                .body(object)
                .asStringAsync(response -> {
                    String message = String.format("Request sent to {{ %s }} had the following response {{ %s }} with status %d", url, response.getBody(), response.getStatus());
                    logger.info(message);
                });
    }

    private void trace(String type, ThirdParty tp, String url, Object obj) {
        String message = String.format("%s sent to ThirdParty %s at the following URL: %s with body: %s", type, tp.getName(), url, jsonTransformer.toJson(obj));
        logger.info(message);
    }

}
