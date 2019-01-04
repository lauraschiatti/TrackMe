package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import avila.schiatti.virdi.resource.api.BulkDataRequest;
import avila.schiatti.virdi.resource.api.IndividualDataRequest;
import avila.schiatti.virdi.resource.api.NotificationRequest;
import avila.schiatti.virdi.utils.JSONObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unirest.Unirest;

import java.util.Collection;

public class APIManager {
    private static Logger logger = LoggerFactory.getLogger(APIManager.class);

    private static final String APPLICATION_JSON = "application/json";

    private APIManager() {
        Unirest.config().setObjectMapper(new JSONObjectMapper());
    }

    public static APIManager create(){
        return new APIManager();
    }

    public void sendNotification(ThirdParty tp, D4HRequest request) {
        if (tp != null && tp.getConfig() != null && tp.getConfig().getNotificationUrl() != null && !tp.getConfig().getNotificationUrl().isEmpty()) {
            String url = tp.getConfig().getNotificationUrl();

            NotificationRequest notification = new NotificationRequest(request);

            this.post(url, notification);
        }
    }

    public void sendData(ThirdParty tp, String ssn, Data data){
        if(tp != null && tp.getConfig() != null && tp.getConfig().getIndividualPushUrl() != null && !tp.getConfig().getIndividualPushUrl().isEmpty()){
            String url = tp.getConfig().getIndividualPushUrl();

            IndividualDataRequest request = new IndividualDataRequest(ssn, data);
            this.post(url, request);
        }
    }

    public void sendData(ThirdParty tp, Collection<Data> bulkData){
        if(tp != null && tp.getConfig() != null && tp.getConfig().getBulkPushUrl() != null && !tp.getConfig().getBulkPushUrl().isEmpty()){
            String url = tp.getConfig().getBulkPushUrl();

            BulkDataRequest data = new BulkDataRequest(bulkData);
            this.post(url, data);
        }
    }

    private void post(String url, Object object) {
        Unirest.post(url)
                .header("accept", APPLICATION_JSON)
                .body(object)
                .asStringAsync(response -> {
                    String message = String.format("Request sent to {{ %s }} had the following response {{ %s }} with status %d", url, response.getBody(), response.getStatus());
                    APIManager.logger.info(message);
                });
    }

}
