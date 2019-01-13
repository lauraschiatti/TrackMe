package avila.schiatti.virdi;

import avila.schiatti.virdi.service.RouteConfig;
import avila.schiatti.virdi.service.Service;
import spark.servlet.SparkApplication;

import static spark.Spark.*;

public final class ASOSApp implements SparkApplication {

    private static final String APPLICATION_JSON = "application/json";

    private static final ASOSApp _instance = new ASOSApp();
    private static final RouteConfig routes = RouteConfig.getInstance();

    public static ASOSApp getInstance() {
        return _instance;
    }

    public ASOSApp createServer(int port) {
        port(port);
        threadPool(100, 10, 30000);
        return this;
    }

    @Override
    public void init() {
        setGlobalExceptionHandlers();
        setRoutes();

        awaitInitialization();
    }

    @Override
    public void destroy() {

    }

    public ASOSApp registerService(Service service) {
        // register the Services that will expose their routes.
        routes.register(service);

        return this;
    }

    private void setRoutes() {
        // set configured api
        routes.setApiEndpoints();
    }

     private void setGlobalExceptionHandlers() {
        // if an internal server error happens, this will catch it.
        internalServerError((req, res) -> {
            res.type(APPLICATION_JSON);
            return "{\"message\":\"Internal server error\"}";
        });
    }
}
