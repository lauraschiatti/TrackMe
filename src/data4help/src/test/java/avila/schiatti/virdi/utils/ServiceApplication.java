package avila.schiatti.virdi.utils;

import avila.schiatti.virdi.service.Service;
import spark.Spark;
import spark.servlet.SparkApplication;

public class ServiceApplication<T extends Service> implements SparkApplication {

    private Service service;

    public ServiceApplication() {
    }

    public ServiceApplication setService(Service service){
        this.service = service;
        return this;
    }

    public ServiceApplication createServer(int port){
        Spark.port(port);
        Spark.threadPool(1000, 1000,60000);
        return this;
    }


    @Override
    public void init() {
        service.setupApiEndpoints();
        service.setupWebEndpoints();
        service.setupExceptionHandlers();
        Spark.get("/ping", (req, res) -> "pong");
        Spark.awaitInitialization();
    }
}