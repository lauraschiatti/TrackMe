package avila.schiatti.virdi.configuration;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public final class StaticConfiguration {
    private final static ProcessBuilder processBuilder = new ProcessBuilder();
    private final static Map<String, String> configVars = processBuilder.environment();

    private final static Logger logger = LoggerFactory.getLogger(StaticConfiguration.class);
    private final static String DEFAULT_MONGODB_URI = "mongodb://127.0.0.1:27017";
    private final static String DEFAULT_MONGODB_DATABASE = "automatedsos";
    private final static Integer DEFAULT_PORT = 5678;

    private static StaticConfiguration _instance = null;

    private static String DATABASE_NAME;
    private static String LOCAL_MONGODB_URI;
    private static Integer PORT;

    private StaticConfiguration() {
    }

    ;

    public static StaticConfiguration getInstance() {
        if (_instance == null) {
            _instance = new StaticConfiguration();

            _instance.configureConstants();
        }
        return _instance;
    }

    private void configureConstants() {
        try {
            PORT = (configVars.get("PORT") != null) ? Integer.parseInt(configVars.get("PORT")) : DEFAULT_PORT;
            DATABASE_NAME = (configVars.get("MONGODB_DATABASE") != null) ? configVars.get("MONGODB_DATABASE") : DEFAULT_MONGODB_DATABASE;
            LOCAL_MONGODB_URI = (configVars.get("MONGODB_URI") != null) ? configVars.get("MONGODB_URI") : DEFAULT_MONGODB_URI;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            DATABASE_NAME = DEFAULT_MONGODB_DATABASE;
            LOCAL_MONGODB_URI = DEFAULT_MONGODB_URI;
            PORT = DEFAULT_PORT;
        }
    }

    @NotNull
    public Integer getPort() {
        return PORT;
    }

    public String getMongoDBConnectionString() {
        return LOCAL_MONGODB_URI;
    }

    public String getMongoDBDatabase() {
        return DATABASE_NAME;
    }

}
