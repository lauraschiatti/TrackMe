package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.user.ASOSUser;
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

    public void sendNotification(ASOSUser user, Data request) {
        EmergencyContact contact = user.getContact();
        String message = String.format("Information sent to %s at URL %s for the user with SSN %s with the following information %s", contact.getName(), contact.getUrl(), user.getSsn(),jsonTransformer.toJson(request));
        logger.info(message);
    }
}
