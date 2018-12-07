package avila.schiatti.virdi.service;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import avila.schiatti.virdi.service.request.LoginRequest;
import avila.schiatti.virdi.service.request.LogoutRequest;
import avila.schiatti.virdi.service.response.ErrorResponse;
import avila.schiatti.virdi.service.response.LoginResponse;
import avila.schiatti.virdi.utils.Data4HelpApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jdk.nashorn.internal.ir.annotations.Ignore;
import net.sf.corn.httpclient.HttpClient;
import net.sf.corn.httpclient.HttpResponse;
import org.junit.jupiter.api.*;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;
import xyz.morphia.query.Query;

import java.net.URI;

import static net.sf.corn.httpclient.HttpClient.HTTP_METHOD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class LoginServiceTest {

    private static final StaticConfiguration config = StaticConfiguration.getInstance();
    private static final Integer PORT = 8888;
    private static final String TEST_APP_URL = "http://localhost:"+PORT+"/web";
    private static final String TESTING_REDIS_DB = "10";
    private final static String MODELS_PACKAGE = "avila.schiatti.virdi.model";
    private final static String MONGODB_TEST_DATABASE = "test_data4help";

    private static final String SSN = "testing_ssn_number";
    private static final String INDIVIDUAL_NAME = "John Doe";
    private static final String INDIVIDUAL_EMAIL = "my_personal_email@address.com";
    private static final String PASSWORD = "My Pa22w0rd";

    private static Datastore datastore;
    private static RedisCommands<String, String> commands;

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
        commands = connection.sync();

        AuthenticationManager.createForTestingOnly(commands);
    }

    private static UserResource setupUserResource() {
        DBManager dbManager = mock(DBManager.class);
        UserResource userResource = new UserResource(dbManager);
        datastore = createDatastore();
        when(userResource.getDatastore()).thenReturn(datastore);

        return userResource;
    }

    private Individual createIndividualUser(){
        Individual i = new Individual();
        i.setSsn(SSN);
        i.setName(INDIVIDUAL_NAME);
        i.setEmail(INDIVIDUAL_EMAIL);
        i.setPassword(AuthenticationManager.hashPassword(PASSWORD));

        datastore.save(i);
        return i;
    }

    private <T> T doLogin(String email, String password, Class<T> clazz) throws Exception{
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        HttpClient client = new HttpClient(new URI(TEST_APP_URL + "/login"));
        HttpResponse response = client.sendData(HTTP_METHOD.POST, jsonTransformer.toJson(loginRequest));

        return jsonTransformer.fromJson(response.getData(), clazz);
    }

    private String doLogout(String accessToken) throws Exception {
        LogoutRequest request = new LogoutRequest(accessToken);

        HttpClient client = new HttpClient(new URI(TEST_APP_URL + "/logout"));
        HttpResponse response = client.sendData(HTTP_METHOD.POST, jsonTransformer.toJson(request));

        return response.getData();
    }

    private static Data4HelpApp app = new Data4HelpApp();
    private static Gson jsonTransformer = new GsonBuilder().create();

    @BeforeAll
    public static void beforeAll(){
        setupAuthManager();
        LoginService service = new LoginService(AuthenticationManager.getInstance(), setupUserResource());

        app.createServer(PORT)
                .registerService(service)
                .init();
    }

    @AfterAll
    public static void afterAll() {
        app.destroy();
    }

    @AfterEach
    public void afterEach(){
        // remove all created users.
        Query<D4HUser> query = datastore.createQuery(D4HUser.class);
        datastore.delete(query);
        // clean redis
        commands.flushdb();
    }

    @Test
    @DisplayName("Test /web/login endpoint using existing email and password")
    public void testLoginEndpointWithCorrectEmailAndPass(){
        try {
            Individual individual = createIndividualUser();

            LoginResponse response = doLogin(INDIVIDUAL_EMAIL, PASSWORD, LoginResponse.class);

            assertEquals(individual.getId().toString(), response.getUserId());

            String userId = commands.get(response.getAccessToken());
            long ttl = commands.ttl(response.getAccessToken());

            assertEquals(individual.getId().toString(), userId);
            assertEquals(3600L, ttl);
        }catch (Exception ex){
            fail(ex.getMessage());
        }
    }

    @Test
    @DisplayName("Test /web/login endpoint using existing not valid email and password")
    public void testLoginEndpointWithWrongEmail(){
        try {
            createIndividualUser();
            ErrorResponse response = doLogin("not_valid@email.com", PASSWORD, ErrorResponse.class);

            assertEquals(response.getMessage(), TrackMeError.NOT_VALID_EMAIL_OR_PASSWORD.getMessage());
        }catch (Exception ex){
            fail(ex.getMessage());
        }
    }

    @Test
    @DisplayName("Test /web/login endpoint using existing email and not valid password")
    public void testLoginEndpointWithWrongPassword(){
        try {
            createIndividualUser();
            ErrorResponse result = doLogin(INDIVIDUAL_EMAIL, "not_valid_password", ErrorResponse.class);

            assertEquals(result.getMessage(), TrackMeError.NOT_VALID_EMAIL_OR_PASSWORD.getMessage());
        }catch (Exception ex){
            fail(ex.getMessage());
        }
    }

    @Test
    @Ignore
    @DisplayName("Test /web/logout endpoint using existing access token")
    public void testLogoutEndpointWithCorrectAT(){
        try {
            // prerequisites
            Individual individual = createIndividualUser();
            LoginResponse loginResponse = doLogin(INDIVIDUAL_EMAIL, PASSWORD, LoginResponse.class);
            String userId = commands.get(loginResponse.getAccessToken());
            assertEquals(individual.getId().toString(), userId);
            //-------------------------
            // do logout
            doLogout(loginResponse.getAccessToken());
            String notFoundId = commands.get(loginResponse.getAccessToken());
            assertNull(notFoundId);

        }catch (Exception ex){
            fail(ex.getMessage());
        }
    }

    @Test
    @DisplayName("Test /web/logout endpoint using not valid access token")
    public void testLogoutEndpointWithWrongAT(){
        String NOT_VALID_ACCESS_TOKEN = "not_valid_access_token";
        try {
            // prerequisites
            Individual individual = createIndividualUser();
            LoginResponse loginResponse = doLogin(INDIVIDUAL_EMAIL, PASSWORD, LoginResponse.class);
            String userId = commands.get(loginResponse.getAccessToken());
            assertEquals(individual.getId().toString(), userId);
            //-------------------------
            // do logout
            doLogout(NOT_VALID_ACCESS_TOKEN);

            // nothing change..
            userId = commands.get(loginResponse.getAccessToken());
            assertEquals(individual.getId().toString(), userId);
        }catch (Exception ex){
            fail(ex.getMessage());
        }
    }
}
