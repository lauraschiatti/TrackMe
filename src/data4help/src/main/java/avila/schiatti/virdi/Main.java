package avila.schiatti.virdi;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.service.authentication.AuthenticationService;

import static spark.Spark.*;

public class Main {
    private final static String STATUS_URL = "/status";

    private static final AuthenticationService authenticationService = AuthenticationService.getInstance();
    private static final StaticConfiguration config = StaticConfiguration.getInstance();

    public static void main(String[] args) {
        port(config.getPort());

        setAuthHandlers();
        setGlobalExceptionHandlers();
        setExceptionHandlers();
        setSpecialRoutes();
        setRoutes();
    }

    private static void setSpecialRoutes() {
        get(STATUS_URL, (req, res) -> "OK");
    }

    private static void setGlobalExceptionHandlers() {
        internalServerError((req, res) -> {
            res.type("application/json");
            return "{\"message\":\"Internal server error\"}";
        });
    }

    private static void setRoutes() {

    }

    private static void setExceptionHandlers() {
//        bookResource.setupExceptionHandler();
    }

    private static void setAuthHandlers() {
        // everything done from the front-end should pass through WEB endpoint
        before("/web/*", (req, res) -> {
            String accessToken = req.headers("ACCESS-TOKEN");
            String userId = req.headers("USER_ID");

            try {
                authenticationService.validateAndUpdateAccessToken(userId, accessToken);
            } catch (TrackMeException ex) {
                halt(ex.getCode(), ex.toJsonString());
            }
        });

        // ONLY third party companies has access to the API endpoints
        before("/api/*", (req, res) -> {
            String secretKey = req.headers("SECRET_KEY");
            // APP ID can be sent via query parameters or headers
            String appId = req.queryParams("app_id") != null ? req.queryParams("app_id") : req.headers("APP_ID");

            try {
                authenticationService.validateSecretKey(appId, secretKey);
            } catch (TrackMeException ex) {
                halt(ex.getCode(), ex.toJsonString());
            }
        });
    }
}
