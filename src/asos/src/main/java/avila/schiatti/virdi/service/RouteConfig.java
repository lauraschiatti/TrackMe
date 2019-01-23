package avila.schiatti.virdi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static spark.Spark.before;
import static spark.Spark.path;

public class RouteConfig {
    private Logger logger = LoggerFactory.getLogger(RouteConfig.class);
    private static RouteConfig _instance;

    private ArrayList<Service> services;

    private RouteConfig(){
        services = new ArrayList<>();
    }

    public static RouteConfig getInstance(){
        if(_instance == null){
            _instance = new RouteConfig();
        }
        return _instance;
    }

    public RouteConfig register(Service serviceInstance){
        services.add(serviceInstance);
        return this;
    }

    public void setApiEndpoints(){
        before((req, res)->{
            res.type("application/json");
        });

        path("/api", () -> {
            for (Service service : services) {
                String infoMessage = "Service: ".concat(service.getClass().getName()).concat(" setting up API endpoints..");
                logger.info(infoMessage);
                service.setupApiEndpoints();
            }
        });
    }

    public void destroy(){
        services.clear();
    }
}
