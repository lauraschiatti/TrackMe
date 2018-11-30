package avila.schiatti.virdi.utils;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.LoginService;
import avila.schiatti.virdi.service.LoginServiceTest;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import avila.schiatti.virdi.service.Service;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import spark.Spark;
import spark.servlet.SparkApplication;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceApplication<T extends Service> implements SparkApplication {

    private Service service;

    public ServiceApplication() {
    }

    public ServiceApplication setService(Service service){
        this.service = service;
        return this;
    }

    public ServiceApplication createServer(){
        Spark.port(8888);
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