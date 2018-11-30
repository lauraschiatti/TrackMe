package avila.schiatti.virdi;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.exception.ValidationException;
import avila.schiatti.virdi.service.LoginService;
import avila.schiatti.virdi.service.RouteConfig;
import avila.schiatti.virdi.service.SignupService;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import avila.schiatti.virdi.utils.SparkUtils;
import org.eclipse.jetty.http.HttpStatus;
import spark.servlet.SparkApplication;

import static spark.Spark.*;

public class Data4HelpApp {
    private final static String STATUS_URL = "/status";
    private static final String APPLICATION_JSON = "application/json";

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
        // register the Services that will expose their routes.
        routes.register(LoginService.create())
                .register(SignupService.create());
    }

    private static void setSpecialRoutes() {
        head(STATUS_URL, (req, res) -> "");
    }

    private static void setGlobalExceptionHandlers() {
        // if an internal server error happens, this will catch it.
        internalServerError((req, res) -> {
            res.type(APPLICATION_JSON);
            return "{\"message\":\"Internal server error\"}";
        });
    }

    private static void setRoutes() {
        // set configured api and web endpoints for all registered services
        routes.setApiEndpoints();
        routes.setWebEndpoints();
    }

    private static void setExceptionHandlers() {
        // catch all the TrackMeExceptions and halt with an error code
        exception(TrackMeException.class, (e, req, res) -> {
            res.type(APPLICATION_JSON);
            halt(e.getStatusCode(), e.toJsonString());
        });

        // to catch any case in which the validation exception is not captured before.
        exception(ValidationException.class, (e, req, res) -> {
            res.type(APPLICATION_JSON);
            halt(HttpStatus.BAD_REQUEST_400, "{\"message\": \""+e.getMessage()+"\"}");
        });
    }

    private static void setAuthHandlers() {
        // everything done from the front-end should pass through WEB endpoint
        before("/web/*", (req, res) -> {
            String path = req.pathInfo();
            String accessToken = req.headers("ACCESS-TOKEN");
            String userId = req.headers("USER_ID");

            // TODO: improve this if.
            if(path.contains("login") == Boolean.FALSE && path.contains("signup") == Boolean.FALSE) {
                authenticationManager.validateAndUpdateAccessToken(userId, accessToken);
            }
        });

        // ONLY third party companies has access to the API endpoints
        before("/api/*", (req, res) -> {
            String secretKey = req.headers("SECRET_KEY");
            // APP ID can be sent via query parameters or headers
            String appId = req.queryParams("app_id") != null ? req.queryParams("app_id") : req.headers("APP_ID");

            authenticationManager.validateSecretKey(appId, secretKey);
        });
    }
}
