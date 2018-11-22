package avila.schiatti.virdi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;

import static spark.Spark.*;

public class RouteConfig {
    private Logger logger = LoggerFactory.getLogger(RouteConfig.class);
    private static RouteConfig _instance;

    private HashMap<Class, Service> services;

    private RouteConfig(){
        services = new HashMap<>();
    }

    public static RouteConfig build(){
        if(_instance == null){
            _instance = new RouteConfig();
        }
        return _instance;
    }

    public <T> RouteConfig add(Class<T> clazz, Service serviceInstance){
        services.put(clazz, serviceInstance);
        return this;
    }

    public void setApiEndpoints(){
        path("/api/", () -> {
            for (HashMap.Entry<Class, Service> entry : services.entrySet()) {
                String infoMessage = "Service: ".concat(entry.getKey().getName()).concat(" setting up API endpoints..");
                logger.info(infoMessage);
                entry.getValue().setupApiEndpoints();
            }
        });
    }

    public void setWebEndpoints(){
        path("/web/", () -> {
            for (HashMap.Entry<Class, Service> entry : services.entrySet()) {
                String infoMessage = "Service: ".concat(entry.getKey().getName()).concat(" setting up WEB endpoints..");
                logger.info(infoMessage);
                entry.getValue().setupWebEndpoints();
            }
        });
    }
}
