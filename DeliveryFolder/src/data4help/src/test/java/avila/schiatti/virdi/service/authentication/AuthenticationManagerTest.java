package avila.schiatti.virdi.service.authentication;

import avila.schiatti.virdi.Data4HelpApp;
import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.exception.ValidationException;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.service.Service;
import avila.schiatti.virdi.service.response.ErrorResponse;
import avila.schiatti.virdi.utils.JSONObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.bson.types.ObjectId;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.*;
import unirest.HttpResponse;
import unirest.Unirest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static spark.Spark.get;

public class AuthenticationManagerTest {

    private static class DummyService extends Service {

        static final String OK_RESPONSE = "OK";
        static final String DUMMY_VALIDATION_EXCEPTION = "dummy validation exception";

        @Override
        public void setupWebEndpoints() {
            get("dummy/endpoint", (req, res) -> {
                res.status(HttpStatus.OK_200);
                return OK_RESPONSE;
            });

            get("some/path/with/login/word/to/avoid/auth/middleware", (req, res) -> {
                throw new ValidationException(DUMMY_VALIDATION_EXCEPTION);
            });
        }

        @Override
        public void setupApiEndpoints() {
            get("dummy/endpoint", (req, res) -> {
                res.status(HttpStatus.OK_200);
                return OK_RESPONSE;
            });
        }

        @Override
        public void setupExceptionHandlers() {
        }
    }

    private static final StaticConfiguration config = StaticConfiguration.getInstance();
    private static final String TESTING_REDIS_DB = "10";
    private static final String ACCESS_TOKEN = "the_user_session_access_token";
    private static final String USER_ID = "the_user_id";
    private static final String SECRET_KEY = "my_company_secret_key";
    private static final String APP_ID = "my_company_app_id";
    private static final String TEST_APP_URL = "http://localhost:8888";

    private static RedisCommands<String, String> commands;
    private static AuthenticationManager authManager;
    private static Data4HelpApp app;

    private static void initD4H() {
        app = Data4HelpApp.getInstance();
        app.setAuthenticationManager(authManager);
        app.createServer(8888)
                .registerService(new DummyService())
                .setAuthHandlers()
                .init();
    }

    @BeforeAll
    public static void beforeAll() {
        String redisConnectionString = config.getRedisUrl().concat(TESTING_REDIS_DB);
        RedisClient redisClient = RedisClient.create(redisConnectionString);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        commands = connection.sync();

        AuthenticationManager.createForTestingOnly(commands);
        authManager = AuthenticationManager.getInstance();

        Unirest.config().setObjectMapper(new JSONObjectMapper());

        initD4H();
    }

    @AfterAll
    public static void afterAll() {
        app.destroy();
    }

    @AfterEach
    public void afterEach() {
        commands.flushdb();
    }

    @Test
    @DisplayName("Test if validateAndUpdateAccessToken validates ok")
    public void testIfValidateAndUpdateAccessTokenOk() {
        try {
            createAndStoreToken();

            authManager.validateAndUpdateAccessToken(USER_ID, ACCESS_TOKEN);
            String userId = commands.get(ACCESS_TOKEN);

            assertEquals(userId, USER_ID);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    @DisplayName("Test if validateAndUpdateAccessToken validates ok the token and updates the TTL")
    public void testIfValidateAndUpdateAccessTokenUpdatesTTL() {
        try {
            createAndStoreToken();

            // time sleep for 1 second
            Thread.sleep(1000);

            authManager.validateAndUpdateAccessToken(USER_ID, ACCESS_TOKEN);
            String userId = commands.get(ACCESS_TOKEN);
            Long remainingTTL = commands.ttl(ACCESS_TOKEN);

            assertEquals(userId, USER_ID);
            assertEquals(AuthenticationManager.DEFAULT_TTL_SECONDS, remainingTTL);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    @DisplayName("Test if validateAndUpdateAccessToken throw exception when userId is null")
    public void testIfValidateAndUpdateAccessTokenThrowExceptionWhenUserIsNull() {
        try {
            createAndStoreToken();

            authManager.validateAccessToken(null, ACCESS_TOKEN);

            fail("TrackMeException was not thrown");
        } catch (TrackMeException tex) {
            assertEquals(tex.getMessage(), TrackMeError.NOT_VALID_USER.getMessage());
        }
    }

    @Test
    @DisplayName("Test if validateAndUpdateAccessToken throw exception when userId is empty string")
    public void testIfValidateAndUpdateAccessTokenThrowExceptionWhenUserIsEmptyString() {
        try {
            createAndStoreToken();

            authManager.validateAccessToken("", ACCESS_TOKEN);

            fail("TrackMeException was not thrown");
        } catch (TrackMeException tex) {
            assertEquals(tex.getMessage(), TrackMeError.NOT_VALID_USER.getMessage());
        }
    }

    @Test
    @DisplayName("Test if validateAndUpdateAccessToken throw exception when accessToken is null")
    public void testIfValidateAndUpdateAccessTokenThrowExceptionWhenAccessTokenIsNull() {
        try {
            createAndStoreToken();

            authManager.validateAccessToken(USER_ID, null);

            fail("TrackMeException was not thrown");
        } catch (TrackMeException tex) {
            assertEquals(tex.getMessage(), TrackMeError.NULL_TOKEN.getMessage());
        }
    }

    @Test
    @DisplayName("Test if validateAndUpdateAccessToken throw exception when accessToken is empty string")
    public void testIfValidateAndUpdateAccessTokenThrowExceptionWhenAccessTokenIsEmptyString() {
        try {
            createAndStoreToken();

            authManager.validateAccessToken(USER_ID, "");

            fail("TrackMeException was not thrown");
        } catch (TrackMeException tex) {
            assertEquals(tex.getMessage(), TrackMeError.NULL_TOKEN.getMessage());
        }
    }

    @Test
    @DisplayName("Test if validateAndUpdateAccessToken throw exception when the userid does not correspond with the access token")
    public void testIfValidateAndUpdateAccessTokenThrowExceptionWhenUserIdDoesNotCorrespondWithTheAT() {
        try {
            createAndStoreToken();

            authManager.validateAccessToken("not_valid_user_id", ACCESS_TOKEN);

            fail("TrackMeException was not thrown");
        } catch (TrackMeException tex) {
            assertEquals(tex.getMessage(), TrackMeError.NOT_VALID_USER.getMessage());
        }
    }

    @Test
    @DisplayName("Test if validateAndUpdateAccessToken throw exception when the access token does not exist")
    public void testIfValidateAndUpdateAccessTokenThrowExceptionWhenNotValidToken() {
        try {
            createAndStoreToken();

            authManager.validateAccessToken(USER_ID, "not_valid_access_token");

            fail("TrackMeException was not thrown");
        } catch (TrackMeException tex) {
            assertEquals(tex.getMessage(), TrackMeError.NOT_VALID_SESSION.getMessage());
        }
    }

    @Test
    @DisplayName("Test if validateSecretKey validates the secret and app id ok")
    public void testIfValidateSecretKeyValidateOK() {
        try {
            createAndStoreScretKeyAndAppId();

            authManager.validateSecretKey(APP_ID, SECRET_KEY);

            String appId = commands.get(SECRET_KEY);

            assertEquals(appId, APP_ID);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    @DisplayName("Test if validateSecretKey throws exception when appId is null")
    public void testIfValidateSecretKeyThrowsExceptionWhenAppIdisNull() {
        try {
            createAndStoreScretKeyAndAppId();

            authManager.validateSecretKey(null, SECRET_KEY);

            fail();
        } catch (TrackMeException tex) {
            assertEquals(tex.getMessage(), TrackMeError.NOT_VALID_SECRET_KEY.getMessage());
        }
    }

    @Test
    @DisplayName("Test if validateSecretKey throws exception when appId is empty string")
    public void testIfValidateSecretKeyThrowsExceptionWhenAppIdisEmptyString() {
        try {
            createAndStoreScretKeyAndAppId();

            authManager.validateSecretKey("", SECRET_KEY);

            fail();
        } catch (TrackMeException tex) {
            assertEquals(tex.getMessage(), TrackMeError.NOT_VALID_SECRET_KEY.getMessage());
        }
    }

    @Test
    @DisplayName("Test if validateSecretKey throws exception when secret key is null")
    public void testIfValidateSecretKeyThrowsExceptionWhenSecretKeyisNull() {
        try {
            createAndStoreScretKeyAndAppId();

            authManager.validateSecretKey(APP_ID, null);

            fail();
        } catch (TrackMeException tex) {
            assertEquals(tex.getMessage(), TrackMeError.NOT_VALID_SECRET_KEY.getMessage());
        }
    }

    @Test
    @DisplayName("Test if validateSecretKey throws exception when secret key is empty string")
    public void testIfValidateSecretKeyThrowsExceptionWhenSecretKeyisEmptyString() {
        try {
            createAndStoreScretKeyAndAppId();

            authManager.validateSecretKey(APP_ID, "");

            fail();
        } catch (TrackMeException tex) {
            assertEquals(tex.getMessage(), TrackMeError.NOT_VALID_SECRET_KEY.getMessage());
        }
    }

    @Test
    @DisplayName("Test if validateSecretKey throws exception when the app id is not valid")
    public void testIfValidateSecretKeyThrowsExceptionWhenNotValidAppId() {
        try {
            createAndStoreScretKeyAndAppId();

            authManager.validateSecretKey("not_valid_app_id", SECRET_KEY);

            fail();
        } catch (TrackMeException tex) {
            assertEquals(tex.getMessage(), TrackMeError.NOT_VALID_SECRET_KEY.getMessage());
        }
    }

    @Test
    @DisplayName("Test if validateSecretKey throws exception when the secret key is not valid")
    public void testIfValidateSecretKeyThrowsExceptionWhenNotValidSecretKey() {
        try {
            createAndStoreScretKeyAndAppId();

            authManager.validateSecretKey(APP_ID, "not_valid_secret_key");

            fail();
        } catch (TrackMeException tex) {
            assertEquals(tex.getMessage(), TrackMeError.NOT_VALID_SECRET_KEY.getMessage());
        }
    }

    @Test
    @DisplayName("Test that setUserAccessToken creates the access token for a given individual")
    public void testIfSetUserAccessTokenCreatesAndStoresAccessTokenForGivenIndividual() {
        ObjectId objectId = new ObjectId();
        Individual individual = createIndividual(objectId);
        AuthenticationManager spyAuthManager = spy(authManager);

        when(spyAuthManager.createToken(anyString())).thenReturn(ACCESS_TOKEN);

        UserWebAuth auth = spyAuthManager.setUserAccessToken(individual);

        assertEquals(auth.getUserId(), objectId.toString());
        assertEquals(auth.getAccessToken(), ACCESS_TOKEN);

        String userId = commands.get(ACCESS_TOKEN);
        Long ttl = commands.ttl(ACCESS_TOKEN);

        assertEquals(objectId.toString(), userId);
        assertEquals(ttl, AuthenticationManager.DEFAULT_TTL_SECONDS);
    }

    @Test
    @DisplayName("Test that setThirdPartySecretKey creates the secret key and app id for a third party")
    public void testIfSetThirdPartySecretKeyCreatesAndStoresSecretKeyForGivenThirdParty() {
        String seed = "seed_value";
        String APP_ID = "752a2cde31a23eeae28f59d7ad3762a1";
        AuthenticationManager spyAuthManager = spy(authManager);

        when(spyAuthManager.createToken(anyString())).thenReturn(SECRET_KEY);

        ThirdPartyApiAuth auth = spyAuthManager.setThirdPartySecretKey(seed);

        assertEquals(auth.getAppId(), APP_ID);
        assertEquals(auth.getSecretKey(), SECRET_KEY);

        String appId = commands.get(SECRET_KEY);
        Long ttl = commands.ttl(SECRET_KEY);

        assertEquals(appId, APP_ID);
        assertEquals(ttl, Long.valueOf(-1L));
    }

    @Test
    @DisplayName("Test that deleteAccessToken removes the access token from the DB")
    public void testIfDeleteAccessTokenIsOk() {
        createAndStoreToken();

        authManager.deleteAccessToken(ACCESS_TOKEN);

        String userId = commands.get(ACCESS_TOKEN);
        assertNull(userId);
    }

    @Test
    @DisplayName("Test authentication middleware when calling web endpoints")
    public void testAuthOfWebEndpoints() {
        createAndStoreToken();

        HttpResponse<String> res = Unirest.get(TEST_APP_URL + "/web/dummy/endpoint")
                .header("USER_ID", USER_ID)
                .header("ACCESS-TOKEN", ACCESS_TOKEN)
                .asString();

        assertEquals(res.getBody(), DummyService.OK_RESPONSE);

    }

    @Test
    @DisplayName("Test not valid user id")
    public void testNotValidUserId() {
        createAndStoreToken();

        HttpResponse<ErrorResponse> res = Unirest.get(TEST_APP_URL + "/web/dummy/endpoint")
                .header("USER_ID", "not_valid_user_id")
                .header("ACCESS-TOKEN", ACCESS_TOKEN)
                .asObject(ErrorResponse.class);

        assertEquals(res.getBody().getMessage(), TrackMeError.NOT_VALID_USER.getMessage());

    }

    @Test
    @DisplayName("Test null user id")
    public void testNullUserId() {
        createAndStoreToken();

        HttpResponse<ErrorResponse> res = Unirest.get(TEST_APP_URL + "/web/dummy/endpoint")
                .header("ACCESS-TOKEN", ACCESS_TOKEN)
                .asObject(ErrorResponse.class);

        assertEquals(res.getBody().getMessage(), TrackMeError.NOT_VALID_USER.getMessage());

    }

    @Test
    @DisplayName("Test not valid access token")
    public void testNotValidAT() {
        createAndStoreToken();

        HttpResponse<ErrorResponse> res = Unirest.get(TEST_APP_URL + "/web/dummy/endpoint")
                .header("USER_ID", USER_ID)
                .header("ACCESS-TOKEN", "not_valid_access_token")
                .asObject(ErrorResponse.class);

        assertEquals(res.getBody().getMessage(), TrackMeError.NOT_VALID_SESSION.getMessage());
    }

    @Test
    @DisplayName("Test empty access token")
    public void testEmptyAccessToken() {
        createAndStoreToken();
        HttpResponse<ErrorResponse> res = Unirest.get(TEST_APP_URL + "/web/dummy/endpoint")
                .header("USER_ID", USER_ID)
                .header("ACCESS-TOKEN", "")
                .asObject(ErrorResponse.class);

        assertEquals(res.getBody().getMessage(), TrackMeError.NULL_TOKEN.getMessage());

    }

    @Test
    @DisplayName("Test null access token")
    public void testNullAccessToken() {
        createAndStoreToken();
        HttpResponse<ErrorResponse> res = Unirest.get(TEST_APP_URL + "/web/dummy/endpoint")
                .header("USER_ID", USER_ID)
                .asObject(ErrorResponse.class);

        assertEquals(res.getBody().getMessage(), TrackMeError.NULL_TOKEN.getMessage());

    }

    @Test
    @DisplayName("Test sending secret key and app it to api calls")
    public void testApiCallsWithSecretAndAppId() {
        createAndStoreScretKeyAndAppId();

        HttpResponse<String> res = Unirest.get(TEST_APP_URL + "/api/dummy/endpoint")
                .header("SECRET_KEY", SECRET_KEY)
                .header("APP_ID", APP_ID)
                .asString();

        assertEquals(res.getBody(), DummyService.OK_RESPONSE);
    }

    @Test
    @DisplayName("Test null secret key")
    public void testNullSecretKey() {
        createAndStoreScretKeyAndAppId();

        HttpResponse<ErrorResponse> res = Unirest.get(TEST_APP_URL + "/api/dummy/endpoint")
                .header("APP_ID", APP_ID)
                .asObject(ErrorResponse.class);

        assertEquals(res.getBody().getMessage(), TrackMeError.NOT_VALID_SECRET_KEY.getMessage());
    }

    @Test
    @DisplayName("Test null app id")
    public void testNullAppId() {
        createAndStoreScretKeyAndAppId();

        HttpResponse<ErrorResponse> res = Unirest.get(TEST_APP_URL + "/api/dummy/endpoint")
                .header("SECRET_KEY", SECRET_KEY)
                .asObject(ErrorResponse.class);

        assertEquals(res.getBody().getMessage(), TrackMeError.NOT_VALID_SECRET_KEY.getMessage());
    }

    @Test
    @DisplayName("Test sending not valid secret key")
    public void testNotValidSecret() {
        createAndStoreScretKeyAndAppId();

        HttpResponse<ErrorResponse> res = Unirest.get(TEST_APP_URL + "/api/dummy/endpoint")
                .header("SECRET_KEY", "not_valid_secret_key")
                .header("APP_ID", APP_ID)
                .asObject(ErrorResponse.class);

        assertEquals(res.getBody().getMessage(), TrackMeError.NOT_VALID_SECRET_KEY.getMessage());
    }

    @Test
    @DisplayName("Test sending not valid app id to api calls")
    public void testNotValidAppId() {
        createAndStoreScretKeyAndAppId();

        HttpResponse<ErrorResponse> res = Unirest.get(TEST_APP_URL + "/api/dummy/endpoint")
                .header("SECRET_KEY", SECRET_KEY)
                .header("APP_ID", "not_valid_app_id")
                .asObject(ErrorResponse.class);

        assertEquals(res.getBody().getMessage(), TrackMeError.NOT_VALID_SECRET_KEY.getMessage());
    }

    @Test
    @DisplayName("Test uncached validation exceptions")
    public void testUncachedValidationExceptions() {
        HttpResponse<ErrorResponse> res = Unirest.get(TEST_APP_URL + "/web/some/path/with/login/word/to/avoid/auth/middleware")
                .asObject(ErrorResponse.class);

        assertEquals(res.getStatus(), HttpStatus.BAD_REQUEST_400);
        assertEquals(res.getBody().getMessage(), DummyService.DUMMY_VALIDATION_EXCEPTION);
    }

    private Individual createIndividual(ObjectId objectId) {
        Individual i = new Individual();
        i.setId(objectId);
        return i;
    }

    private void createAndStoreToken() {
        SetArgs args = SetArgs.Builder.ex(AuthenticationManager.DEFAULT_TTL_SECONDS);
        commands.set(ACCESS_TOKEN, USER_ID, args);
    }

    private void createAndStoreScretKeyAndAppId() {
        commands.set(SECRET_KEY, APP_ID);
    }
}
