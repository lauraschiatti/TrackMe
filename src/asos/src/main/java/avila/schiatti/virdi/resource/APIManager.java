package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.user.EmergencyContact;
import avila.schiatti.virdi.utils.JSONObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unirest.Unirest;

public class APIManager {
    private static final Gson jsonTransformer = JSONObjectMapper.jsonTransformer;
    private static Logger logger = LoggerFactory.getLogger(APIManager.class);

    private APIManager() {
        Unirest.config().setObjectMapper(new JSONObjectMapper());
    }

    public static APIManager create() {
        return new APIManager();
    }

    public void sendNotification(EmergencyContact contact, Object request) {
        String message = String.format("Information sent to %s at URL %s with the following information %s", contact.getName(), contact.getUrl(), jsonTransformer.toJson(request));
        logger.info(message);
    }
}
