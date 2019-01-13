package avila.schiatti.virdi.service;

import avila.schiatti.virdi.Data4HelpApp;
import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.ValidationError;
import avila.schiatti.virdi.model.data.Address;
import avila.schiatti.virdi.model.data.BloodType;
import avila.schiatti.virdi.model.data.Gender;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import avila.schiatti.virdi.service.request.IndividualSignupRequestForTest;
import avila.schiatti.virdi.service.request.ThirdPartySignupRequest;
import avila.schiatti.virdi.service.response.ErrorResponse;
import avila.schiatti.virdi.service.response.SignupResponse;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


public class SignupServiceTest {

    private static final StaticConfiguration config = StaticConfiguration.getInstance();
    private static final Integer PORT = 8888;
    private static final String TEST_APP_URL = "http://localhost:" + PORT + "/web";
    private static final String TESTING_REDIS_DB = "10";
    private final static String MODELS_PACKAGE = "avila.schiatti.virdi.model";
    private final static String MONGODB_TEST_DATABASE = "test_data4help";

    // individual
    private static final String SSN = "testing_ssn_number";
    private static final String INDIVIDUAL_NAME = "John Doe";
    private static final String INDIVIDUAL_EMAIL = "my_personal_email@address.com";
    private static final String PASSWORD = "My Pa22w0rd";
    private static final LocalDate INDIVIDUAL_BIRTHDATE = LocalDate.now();
    private static final Gender INDIVIDUAL_GENDER = Gender.MALE;
    private static final Address INDIVIDUAL_ADDRESS = new Address();
    private static final BloodType INDIVIDUAL_BLOOD_TYPE = BloodType.A_POSITIVE;
    private static final Long DEFAULT_TTL = 3600L;

    // third party
    private static final String COMPANY_EMAIL = "my_company@email.com";
    private static final String COMPANY_NAME = "my company name inc.";
    private static final String COMPANY_PHONE = "+393332233123";
    private static final String COMPANY_TAX_CODE = "2344COMPANYTAXCODE";

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

        return new UserResource(datastore);
    }

    private <T> T doIndividualSignup(IndividualSignupRequestForTest body, Class<T> clazz) {
        HttpResponse<T> response = Unirest.post(TEST_APP_URL + "/individual/signup").body(body).asObject(clazz);
        return response.getBody();
    }

    private <T> T doThirdPartySignup(ThirdPartySignupRequest body, Class<T> clazz) {
        HttpResponse<T> response = Unirest.post(TEST_APP_URL + "/thirdparty/signup").body(body).asObject(clazz);
        return response.getBody();
    }

    private static Data4HelpApp app;

    @BeforeAll
    public static void beforeAll() {
        Unirest.config().setObjectMapper(new JSONObjectMapper());
        setupAuthManager();
        SignupService service = new SignupService(setupUserResource(), AuthenticationManager.getInstance());

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
    @DisplayName("Test /web/individual/signup is ok")
    public void testIndividualSignupOK() {
        IndividualSignupRequestForTest req = new IndividualSignupRequestForTest();
        req.setAddress(INDIVIDUAL_ADDRESS);
        req.getAddress().setCity("city");
        req.getAddress().setProvince("province");
        req.getAddress().setCountry("country");
        req.setBirthDate(INDIVIDUAL_BIRTHDATE);
        req.setBloodType(INDIVIDUAL_BLOOD_TYPE);
        req.setEmail(INDIVIDUAL_EMAIL);
        req.setGender(INDIVIDUAL_GENDER);
        req.setName(INDIVIDUAL_NAME);
        req.setPassword(PASSWORD);
        req.setSsn(SSN);

        SignupResponse res = doIndividualSignup(req, SignupResponse.class);
        Individual i = datastore.find(Individual.class).field("ssn").equal(req.getSsn()).get();

        // the user is stored in the DB
        assertEquals(i.getSsn(), SSN);
        assertEquals(i.getName(), INDIVIDUAL_NAME);
        assertEquals(i.getEmail(), INDIVIDUAL_EMAIL);
        assertEquals(i.getId().toString(), res.getUserId());
        // the user is stored in the RedisDB
        String userId = commands.get(res.getAccessToken());
        Long ttl = commands.ttl(res.getAccessToken());

        assertEquals(userId, res.getUserId());
        assertEquals(ttl, DEFAULT_TTL);
    }

    @Test
    @DisplayName("Test /web/thirdparty/signup is ok")
    public void testThirdPartySignupOK() {
        ThirdPartySignupRequest req = new ThirdPartySignupRequest();
        req.setEmail(COMPANY_EMAIL);
        req.setName(COMPANY_NAME);
        req.setPassword(PASSWORD);
        req.setPhone(COMPANY_PHONE);
        req.setTaxCode(COMPANY_TAX_CODE);

        SignupResponse res = doThirdPartySignup(req, SignupResponse.class);
        ThirdParty tp = datastore.find(ThirdParty.class).field("email").equal(req.getEmail()).get();

        // the user is stored in the DB
        assertEquals(tp.getName(), COMPANY_NAME);
        assertEquals(tp.getEmail(), COMPANY_EMAIL);
        assertEquals(tp.getId().toString(), res.getUserId());

        // the user is stored in the RedisDB
        String userId = commands.get(res.getAccessToken());
        Long sessionTtl = commands.ttl(res.getAccessToken());
        String appId = commands.get(tp.getSecretKey());
        long ttl = commands.ttl(tp.getSecretKey());

        // check if the login session token is ok.
        assertEquals(userId, res.getUserId());
        assertEquals(sessionTtl, DEFAULT_TTL);

        // check if it has created a secret key + app id
        assertEquals(appId, tp.getAppId());
        assertEquals(ttl, -1L);
    }

    @Test
    @DisplayName("Test /web/individual/signup returns an error when not valid email is passed")
    public void testIfSignupReturnsAnErrorWhenInvalidEmail() {
        IndividualSignupRequestForTest req = new IndividualSignupRequestForTest();
        req.setAddress(INDIVIDUAL_ADDRESS);
        req.getAddress().setCity("city");
        req.getAddress().setProvince("province");
        req.getAddress().setCountry("country");
        req.setBirthDate(INDIVIDUAL_BIRTHDATE);
        req.setBloodType(INDIVIDUAL_BLOOD_TYPE);
        req.setEmail("not_valid_email");
        req.setGender(INDIVIDUAL_GENDER);
        req.setName(INDIVIDUAL_NAME);
        req.setPassword(PASSWORD);
        req.setSsn(SSN);

        ErrorResponse res = doIndividualSignup(req, ErrorResponse.class);

        // the user is NOT stored in the DB
        Individual i = datastore.find(Individual.class).field("ssn").equal(req.getSsn()).get();
        assertNull(i);

        // check message
        String expected = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), ValidationError.NOT_VALID_EMAIL.getMessage());
        assertEquals(expected, res.getMessage());
    }

    @Test
    @DisplayName("Test /web/individual/signup returns an error when not valid ssn")
    public void testIfSignupReturnsAnErrorWhenSSNNotPresent() {
        IndividualSignupRequestForTest req = new IndividualSignupRequestForTest();
        req.setAddress(INDIVIDUAL_ADDRESS);
        req.getAddress().setCity("city");
        req.getAddress().setProvince("province");
        req.getAddress().setCountry("country");
        req.setBirthDate(INDIVIDUAL_BIRTHDATE);
        req.setBloodType(INDIVIDUAL_BLOOD_TYPE);
        req.setEmail(INDIVIDUAL_EMAIL);
        req.setGender(INDIVIDUAL_GENDER);
        req.setName(INDIVIDUAL_NAME);
        req.setPassword(PASSWORD);
        req.setSsn(null);

        ErrorResponse res = doIndividualSignup(req, ErrorResponse.class);

        // the user is NOT stored in the DB
        Individual i = datastore.find(Individual.class).field("ssn").equal(req.getSsn()).get();
        assertNull(i);

        // check message
        String expected = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), String.format(ValidationError.NOT_VALID_FIELD.getMessage(), "SSN"));
        assertEquals(expected, res.getMessage());
    }

    @Test
    @DisplayName("Test /web/individual/signup returns an error when not valid name")
    public void testIfSignupReturnsAnErrorWhenEmptyName() {
        IndividualSignupRequestForTest req = new IndividualSignupRequestForTest();
        req.setAddress(INDIVIDUAL_ADDRESS);
        req.getAddress().setCity("city");
        req.getAddress().setProvince("province");
        req.getAddress().setCountry("country");
        req.setBirthDate(INDIVIDUAL_BIRTHDATE);
        req.setBloodType(INDIVIDUAL_BLOOD_TYPE);
        req.setEmail(INDIVIDUAL_EMAIL);
        req.setGender(INDIVIDUAL_GENDER);
        req.setName("");
        req.setPassword(PASSWORD);
        req.setSsn(SSN);

        ErrorResponse res = doIndividualSignup(req, ErrorResponse.class);

        // the user is NOT stored in the DB
        Individual i = datastore.find(Individual.class).field("ssn").equal(req.getSsn()).get();
        assertNull(i);

        // check message
        String expected = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), String.format(ValidationError.NOT_VALID_FIELD.getMessage(), "Name"));
        assertEquals(expected, res.getMessage());
    }

    @Test
    @DisplayName("Test /web/individual/signup returns an error when empty password")
    public void testIfSignupReturnsAnErrorWhenEmptyPassword() {
        IndividualSignupRequestForTest req = new IndividualSignupRequestForTest();
        req.setAddress(INDIVIDUAL_ADDRESS);
        req.getAddress().setCity("city");
        req.getAddress().setProvince("province");
        req.getAddress().setCountry("country");
        req.setBirthDate(INDIVIDUAL_BIRTHDATE);
        req.setBloodType(INDIVIDUAL_BLOOD_TYPE);
        req.setEmail(INDIVIDUAL_EMAIL);
        req.setGender(INDIVIDUAL_GENDER);
        req.setName(INDIVIDUAL_NAME);
        req.setPassword("");
        req.setSsn(SSN);

        ErrorResponse res = doIndividualSignup(req, ErrorResponse.class);

        // the user is NOT stored in the DB
        Individual i = datastore.find(Individual.class).field("ssn").equal(req.getSsn()).get();
        assertNull(i);

        // check message
        String expected = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), String.format(ValidationError.NOT_VALID_FIELD.getMessage(), "Password"));
        assertEquals(expected, res.getMessage());
    }

    @Test
    @DisplayName("Test /web/thirdparty/signup is ok")
    public void test_1() {
        ThirdPartySignupRequest req = new ThirdPartySignupRequest();
        req.setEmail("not_valid_email");
        req.setName(COMPANY_NAME);
        req.setPassword(PASSWORD);
        req.setPhone(COMPANY_PHONE);
        req.setTaxCode(COMPANY_TAX_CODE);

        ErrorResponse res = doThirdPartySignup(req, ErrorResponse.class);
        ThirdParty tp = datastore.find(ThirdParty.class).field("email").equal(req.getEmail()).get();

        // the user is stored in the DB
        assertNull(tp);

        // check message
        String expected = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), ValidationError.NOT_VALID_EMAIL.getMessage());
        assertEquals(expected, res.getMessage());
    }

    @Test
    @DisplayName("Test /web/thirdparty/signup is ok")
    public void test_2() {
        ThirdPartySignupRequest req = new ThirdPartySignupRequest();
        req.setEmail(COMPANY_EMAIL);
        req.setName(null);
        req.setPassword(PASSWORD);
        req.setPhone(COMPANY_PHONE);
        req.setTaxCode(COMPANY_TAX_CODE);

        ErrorResponse res = doThirdPartySignup(req, ErrorResponse.class);
        ThirdParty tp = datastore.find(ThirdParty.class).field("email").equal(req.getEmail()).get();

        // the user is stored in the DB
        assertNull(tp);

        // check message
        String expected = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), String.format(ValidationError.NOT_VALID_FIELD.getMessage(), "Business Name"));
        assertEquals(expected, res.getMessage());
    }

    @Test
    @DisplayName("Test /web/thirdparty/signup is ok")
    public void test_3() {
        ThirdPartySignupRequest req = new ThirdPartySignupRequest();
        req.setEmail(COMPANY_EMAIL);
        req.setName(COMPANY_NAME);
        req.setPassword("");
        req.setPhone(COMPANY_PHONE);
        req.setTaxCode(COMPANY_TAX_CODE);

        ErrorResponse res = doThirdPartySignup(req, ErrorResponse.class);
        ThirdParty tp = datastore.find(ThirdParty.class).field("email").equal(req.getEmail()).get();

        // the user is stored in the DB
        assertNull(tp);

        // check message
        String expected = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), String.format(ValidationError.NOT_VALID_FIELD.getMessage(), "Password"));
        assertEquals(expected, res.getMessage());
    }

    @Test
    @DisplayName("Test /web/thirdparty/signup is ok")
    public void test_4() {
        ThirdPartySignupRequest req = new ThirdPartySignupRequest();
        req.setEmail(COMPANY_EMAIL);
        req.setName(COMPANY_NAME);
        req.setPassword(PASSWORD);
        req.setPhone(COMPANY_PHONE);
        req.setTaxCode("");

        ErrorResponse res = doThirdPartySignup(req, ErrorResponse.class);
        ThirdParty tp = datastore.find(ThirdParty.class).field("email").equal(req.getEmail()).get();

        // the user is stored in the DB
        assertNull(tp);

        // check message
        String expected = String.format(TrackMeError.VALIDATION_ERROR.getMessage(), String.format(ValidationError.NOT_VALID_FIELD.getMessage(), "Tax Code"));
        assertEquals(expected, res.getMessage());
    }

}
