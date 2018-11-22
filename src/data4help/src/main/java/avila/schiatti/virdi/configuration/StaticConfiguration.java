package avila.schiatti.virdi.configuration;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public final class StaticConfiguration {
    /**
     *
     */
    private final static ProcessBuilder processBuilder = new ProcessBuilder();
    private final static Map<String, String> configVars = processBuilder.environment();

    private final static Logger logger = LoggerFactory.getLogger(StaticConfiguration.class);
    private final static String CONFIGURATION_FILE = "configuration/trackme.conf";

    private static StaticConfiguration _instance = null;

    private static String DATABASE_NAME;
    private static String LOCAL_MONGODB_URI;
    private static String REDIS_URL;

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
//            String filePath = new File("").getAbsolutePath();
//            logger.debug(filePath);

            BufferedReader bufferedReader = new BufferedReader(new FileReader(CONFIGURATION_FILE));

            Gson gson = new Gson();
            TrackMeConfiguration tmConfig = gson.fromJson(bufferedReader, TrackMeConfiguration.class);

            logger.info("Setting up configuration variables");

            DATABASE_NAME = tmConfig.getMongoDBName();
            LOCAL_MONGODB_URI = tmConfig.getMongoURI();
            REDIS_URL = tmConfig.getRedisURI().concat(tmConfig.getRedisDBNumber());
        }catch(Exception ex) {
            logger.error(ex.getMessage(), ex);

            DATABASE_NAME = "";
            LOCAL_MONGODB_URI = "";
            REDIS_URL = "";
        }
    }

    @NotNull
    public Integer getPort(){
        return (configVars.get("PORT") != null) ? Integer.parseInt(configVars.get("PORT")) : 4567;
    }

    public String getMongoDBConnectionString(){
        return (configVars.get("MONGODB_URI") != null) ? configVars.get("MONGODB_URI") : LOCAL_MONGODB_URI;
    }

    public String getMongoDBDatabase(){
        return (configVars.get("MONGODB_DATABASE") != null) ? configVars.get("MONGODB_DATABASE") : DATABASE_NAME;
    }

    public String getRedisConnectionString(){
        return (configVars.get("REDIS_URL") != null) ? configVars.get("REDIS_URL") : REDIS_URL;
    }
}