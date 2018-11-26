package avila.schiatti.virdi;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.service.LoginService;
import avila.schiatti.virdi.service.RouteConfig;
import avila.schiatti.virdi.service.SignupService;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import avila.schiatti.virdi.service.response.ErrorResponse;
import avila.schiatti.virdi.utils.JsonUtil;
import avila.schiatti.virdi.utils.SparkUtils;

import static spark.Spark.*;

public class Main {
    private final static String STATUS_URL = "/status";

    private static final AuthenticationManager authenticationManager = AuthenticationManager.getInstance();
    private static final StaticConfiguration config = StaticConfiguration.getInstance();
    private static final RouteConfig routes = RouteConfig.getInstance();

    public static void main(String[] args) {
        port(config.getPort());

        SparkUtils.createServerWithRequestLog();

        configureRoutes();

        setAuthHandlers();
        setGlobalExceptionHandlers();
        setExceptionHandlers();
        setSpecialRoutes();
        setRoutes();
    }

    private static void configureRoutes() {
        routes.register(LoginService.getInstance())
                .register(SignupService.getInstance());
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
        routes.setApiEndpoints();
        routes.setWebEndpoints();
    }

    private static void setExceptionHandlers() {
        exception(TrackMeException.class, (e, req, res) -> {
            res.type("application/json");
            res.body(JsonUtil.toJson(new ErrorResponse(e)));
        });
    }

    private static void setAuthHandlers() {
        // TODO login should not pass through this method
        // everything done from the front-end should pass through WEB endpoint
        before("/web/*", (req, res) -> {
            String path = req.pathInfo();
            String accessToken = req.headers("ACCESS-TOKEN");
            String userId = req.headers("USER_ID");

            try {
                if(path.contains("login") == Boolean.FALSE && path.contains("signup") == Boolean.FALSE) {
                    authenticationManager.validateAndUpdateAccessToken(userId, accessToken);
                }

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
                authenticationManager.validateSecretKey(appId, secretKey);
            } catch (TrackMeException ex) {
                halt(ex.getCode(), ex.toJsonString());
            }
        });
    }
}
