package avila.schiatti.virdi.configuration;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

public final class StaticConfiguration {

    public static final Integer MINIMUM_ANONYMIZE_SIZE = 5;

    private final static ProcessBuilder processBuilder = new ProcessBuilder();
    private final static Map<String, String> configVars = processBuilder.environment();

    private final static Logger logger = LoggerFactory.getLogger(StaticConfiguration.class);
    private final static String CONFIGURATION_FILE = "configuration/trackme.conf";

    private static StaticConfiguration _instance = null;

    private static String DATABASE_NAME;
    private static String LOCAL_MONGODB_URI;
    private static String REDIS_URL;
    private static String REDIS_DB;
    private static String ASOS_URL;
    private static String ASOS_ADDRESS_URL;

    private StaticConfiguration(){ };

    public static StaticConfiguration getInstance() {
        if(_instance == null){
            _instance = new StaticConfiguration();

            _instance.configureConstants();
        }
        return _instance;
    }

    private void configureConstants() {
        try {
            FileReader fd = new FileReader(CONFIGURATION_FILE);
            BufferedReader bufferedReader = new BufferedReader(fd);

            Gson gson = new Gson();
            TrackMeConfiguration tmConfig = gson.fromJson(bufferedReader, TrackMeConfiguration.class);

            logger.info("Setting up configuration variables");

            DATABASE_NAME = (configVars.get("MONGODB_DATABASE") != null) ? configVars.get("MONGODB_DATABASE") : tmConfig.getMongoDBName();
            LOCAL_MONGODB_URI = (configVars.get("MONGODB_URI") != null) ? configVars.get("MONGODB_URI") : tmConfig.getMongoURI();
            REDIS_URL = (configVars.get("REDIS_URL") != null) ? configVars.get("REDIS_URL") : tmConfig.getRedisURI();
            REDIS_DB = (configVars.get("REDIS_DB") != null) ? configVars.get("REDIS_DB") : tmConfig.getRedisDBNumber();
            ASOS_URL = (configVars.get("ASOS_URL") != null) ? configVars.get("ASOS_URL") : tmConfig.getASOSUrl();
            ASOS_ADDRESS_URL = (configVars.get("ASOS_ADDRESS_URL") != null) ? configVars.get("ASOS_ADDRESS_URL") : tmConfig.getASOSAddressURL();
        }catch(Exception ex) {
            logger.error(ex.getMessage(), ex);

            DATABASE_NAME = "";
            LOCAL_MONGODB_URI = "";
            REDIS_URL = "";
            REDIS_DB = "";
            ASOS_URL = "";
            ASOS_ADDRESS_URL = "";
        }

        logger.debug(LOCAL_MONGODB_URI);
        logger.debug(REDIS_URL);
    }

    @NotNull
    public Integer getPort(){
        return (configVars.get("PORT") != null) ? Integer.parseInt(configVars.get("PORT")) : 4567;
    }

    public String getMongoDBConnectionString(){
        return LOCAL_MONGODB_URI;
    }

    public String getMongoDBDatabase(){
        return DATABASE_NAME;
    }

    public String getRedisConnectionString(){
        return getRedisUrl().concat(getRedisDb());
    }

    public String getRedisUrl(){
        return REDIS_URL;
    }

    public String getRedisDb(){
        return REDIS_DB;
    }

    public String getASOSAddressURL(){
        return ASOS_URL.concat(ASOS_ADDRESS_URL);
    }
}
