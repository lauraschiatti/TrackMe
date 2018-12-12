package avila.schiatti.virdi;

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
    public static final String ACCESS_TOKEN = "ACCESS-TOKEN";
    public static final String USER_ID = "USER_ID";
    public static final String SECRET_KEY = "SECRET_KEY";
    public static final String APP_ID = "APP_ID";
    public static final String QUERY_PARAM_APP_ID = "app_id";

    private static AuthenticationManager authenticationManager;
    private static final RouteConfig routes = RouteConfig.getInstance();

    private static Data4HelpApp _instance;

    private Data4HelpApp(){
        authenticationManager = AuthenticationManager.getInstance();
    }

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
        routes.destroy();
        _instance = null;
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
            String accessToken = req.headers(ACCESS_TOKEN);
            String userId = req.headers(USER_ID);

            // TODO: improve this if.
            if(path.contains("login") == Boolean.FALSE && path.contains("signup") == Boolean.FALSE) {
                authenticationManager.validateAndUpdateAccessToken(userId, accessToken);
            }
        });

        // ONLY third party companies has access to the API endpoints
        before("/api/*", (req, res) -> {
            String secretKey = req.headers(SECRET_KEY);
            // APP ID can be sent via query parameters or headers
            String appId = req.queryParams(QUERY_PARAM_APP_ID) != null ? req.queryParams(QUERY_PARAM_APP_ID) : req.headers(APP_ID);

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

    /**
     * Only for testing
     * @param am
     */
    public void setAuthenticationManager(AuthenticationManager am){
        authenticationManager = am;
    }
}