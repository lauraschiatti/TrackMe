package avila.schiatti.virdi.service;

import avila.schiatti.virdi.Data4HelpApp;
import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import avila.schiatti.virdi.service.request.LoginRequest;
import avila.schiatti.virdi.service.response.ErrorResponse;
import avila.schiatti.virdi.service.response.LoginResponse;
import avila.schiatti.virdi.utils.JSONObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.*;
import unirest.HttpResponse;
import unirest.Unirest;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;
import xyz.morphia.query.Query;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


public class LoginServiceTest {

    private static final StaticConfiguration config = StaticConfiguration.getInstance();
    private static final Integer PORT = 8888;
    private static final String TEST_APP_URL = "http://localhost:" + PORT + "/web";
    private static final String TESTING_REDIS_DB = "10";
    private final static String MODELS_PACKAGE = "avila.schiatti.virdi.model";
    private final static String MONGODB_TEST_DATABASE = "test_data4help";

    private static final String SSN = "testing_ssn_number";
    private static final String INDIVIDUAL_NAME = "John Doe";
    private static final String INDIVIDUAL_EMAIL = "my_personal_email@address.com";
    private static final String PASSWORD = "My Pa22w0rd";

    private static Datastore datastore;
    private static RedisCommands<String, String> commands;

    private static Datastore createDatastore() {
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
        datastore = createDatastore();
        UserResource userResource = new UserResource(datastore);

        return userResource;
    }

    private Individual createIndividualUser() {
        Individual i = new Individual();
        i.setSsn(SSN);
        i.setName(INDIVIDUAL_NAME);
        i.setEmail(INDIVIDUAL_EMAIL);
        i.setPassword(AuthenticationManager.hashPassword(PASSWORD));

        datastore.save(i);
        return i;
    }

    private <T> T doLogin(String email, String password, Class<T> clazz) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        HttpResponse<T> response = Unirest.post(TEST_APP_URL + "/login").body(loginRequest).asObject(clazz);
        return response.getBody();
    }

    private String doLogout(String accessToken) {
        HttpResponse<String> response = Unirest.post(TEST_APP_URL + "/logout").header(Data4HelpApp.ACCESS_TOKEN, accessToken).asString();
        return response.getBody();
    }

    private static Data4HelpApp app;

    @BeforeAll
    public static void beforeAll() {
        Unirest.config().setObjectMapper(new JSONObjectMapper());
        setupAuthManager();
        LoginService service = new LoginService(AuthenticationManager.getInstance(), setupUserResource());

        app = Data4HelpApp.getInstance();
        app.createServer(PORT)
                .registerService(service)
                .init();
    }

    @AfterAll
    public static void afterAll() {
        app.destroy();
    }

    @AfterEach
    public void afterEach() {
        // remove all created users.
        Query<D4HUser> query = datastore.createQuery(D4HUser.class);
        datastore.delete(query);
        // clean redis
        commands.flushdb();
    }

    @Test
    @DisplayName("Test /web/login endpoint using existing email and password")
    public void testLoginEndpointWithCorrectEmailAndPass() {
        Individual individual = createIndividualUser();

        LoginResponse response = doLogin(INDIVIDUAL_EMAIL, PASSWORD, LoginResponse.class);

        assertEquals(individual.getId().toString(), response.getUserId());

        String userId = commands.get(response.getAccessToken());
        long ttl = commands.ttl(response.getAccessToken());

        assertEquals(individual.getId().toString(), userId);
        assertEquals(3600L, ttl);
    }

    @Test
    @DisplayName("Test /web/login endpoint using existing not valid email and password")
    public void testLoginEndpointWithWrongEmail() {
        createIndividualUser();
        ErrorResponse response = doLogin("not_valid@email.com", PASSWORD, ErrorResponse.class);

        assertEquals(response.getMessage(), TrackMeError.NOT_VALID_EMAIL_OR_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("Test /web/login endpoint using existing email and not valid password")
    public void testLoginEndpointWithWrongPassword() {
        createIndividualUser();
        ErrorResponse result = doLogin(INDIVIDUAL_EMAIL, "not_valid_password", ErrorResponse.class);

        assertEquals(result.getMessage(), TrackMeError.NOT_VALID_EMAIL_OR_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("Test /web/logout endpoint using existing access token")
    public void testLogoutEndpointWithCorrectAT() {
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
    }

    @Test
    @DisplayName("Test /web/logout endpoint using not valid access token")
    public void testLogoutEndpointWithWrongAT() {
        String NOT_VALID_ACCESS_TOKEN = "not_valid_access_token";
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
    }
}
