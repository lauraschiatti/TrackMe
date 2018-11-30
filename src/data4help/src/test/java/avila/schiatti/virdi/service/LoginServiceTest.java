package avila.schiatti.virdi.service;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import avila.schiatti.virdi.utils.ServiceApplication;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;
import spark.servlet.SparkApplication;
import sun.rmi.runtime.Log;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class LoginServiceTest {

    private static final StaticConfiguration config = StaticConfiguration.getInstance();
    private static final String TESTING_REDIS_DB = "10";

    private final static String MODELS_PACKAGE = "avila.schiatti.virdi.model";
    private final static String MONGODB_TEST_DATABASE = "test_data4help";

    private static Datastore createDatastore(){
        Morphia morphia = new Morphia();
        morphia.mapPackage(MODELS_PACKAGE);
        MongoClientURI mongoClientURI = new MongoClientURI(config.getMongoDBConnectionString());
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        return morphia.createDatastore(mongoClient, MONGODB_TEST_DATABASE);
    }

    private static void setupAuthManager() {
        String redisConnectionString = config.getRedisUrl().concat(TESTING_REDIS_DB);
        RedisClient redisClient = RedisClient.create(redisConnectionString);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();

        AuthenticationManager.createForTestingOnly(commands);
    }

    private static UserResource setupUserResource() {
        DBManager dbManager = mock(DBManager.class);
        UserResource userResource = new UserResource(dbManager);
        Datastore datastore = createDatastore();
        when(userResource.getDatastore()).thenReturn(datastore);

        return userResource;
    }

    private static ServiceApplication<LoginService> sparkApplication = new ServiceApplication<>();

    @BeforeAll
    public static void beforeAll(){
        setupAuthManager();
        LoginService service = new LoginService(AuthenticationManager.getInstance(), setupUserResource());

        sparkApplication.setService(service)
                .createServer()
                .init();
    }

    @AfterAll
    public static void afterAll() {
        Spark.stop();
    }

    @Test
    public void test() throws Exception {

        CloseableHttpClient httpClient = HttpClients.custom()
                .build();

        HttpGet httpGet = new HttpGet("http://localhost:8888/ping");
        CloseableHttpResponse response = httpClient.execute(httpGet);

        int statusCode = response.getStatusLine().getStatusCode();
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        assertEquals(200, statusCode);
        assertEquals("pong", result.toString());

    }

}
