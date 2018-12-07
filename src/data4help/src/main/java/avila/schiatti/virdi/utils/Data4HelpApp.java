package avila.schiatti.virdi.utils;

import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.exception.ValidationException;
import avila.schiatti.virdi.service.RouteConfig;
import avila.schiatti.virdi.service.Service;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import org.eclipse.jetty.http.HttpStatus;
import spark.Spark;
import spark.servlet.SparkApplication;

import static spark.Spark.*;

public class Data4HelpApp implements SparkApplication {
    private final static String STATUS_URL = "/status";
    private static final String APPLICATION_JSON = "application/json";

    private static final AuthenticationManager authenticationManager = AuthenticationManager.getInstance();
    private static final RouteConfig routes = RouteConfig.getInstance();

    private static Data4HelpApp _instance;

    public Data4HelpApp(){}

    public static Data4HelpApp getInstance() {
        if(_instance == null){
            _instance = new Data4HelpApp();
        }
        return _instance;
    }

    public Data4HelpApp setPublicPath(String path){
        Spark.staticFileLocation(path);
        return this;
    }

    public Data4HelpApp createServer(int port){
        Spark.port(port);
        Spark.threadPool(10000, 10,30000);
        return this;
    }

    @Override
    public void init() {
        setGlobalExceptionHandlers();
        setExceptionHandlers();
        setSpecialRoutes();
        setRoutes();

        Spark.awaitInitialization();
    }

    @Override
    public void destroy() {
        Spark.stop();
    }

    public Data4HelpApp registerService(Service service) {
        // register the Services that will expose their routes.
        routes.register(service);

        return this;
    }

    public Data4HelpApp setAuthHandlers() {
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

        return this;
    }

    private void setSpecialRoutes() {
        head(STATUS_URL, (req, res) -> "");
    }

    private void setRoutes() {
        // set configured api and web endpoints for all registered services
        routes.setApiEndpoints();
        routes.setWebEndpoints();
    }

    private void setGlobalExceptionHandlers() {
        // if an internal server error happens, this will catch it.
        internalServerError((req, res) -> {
            res.type(APPLICATION_JSON);
            return "{\"message\":\"Internal server error\"}";
        });
    }

    private void setExceptionHandlers() {
        // catch all the TrackMeExceptions and halt with an error code
        exception(TrackMeException.class, (e, req, res) -> {
            res.type(APPLICATION_JSON);
            res.status(e.getStatusCode());
            res.body(e.toJsonString());
        });

        // to catch any case in which the validation exception is not captured before.
        exception(ValidationException.class, (e, req, res) -> {
            res.type(APPLICATION_JSON);
            res.status(HttpStatus.BAD_REQUEST_400);
            res.body("{\"message\": \""+e.getMessage()+"\"}");
        });
    }
}