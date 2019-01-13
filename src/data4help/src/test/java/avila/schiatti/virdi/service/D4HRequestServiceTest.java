package avila.schiatti.virdi.service;

import avila.schiatti.virdi.Data4HelpApp;
import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.request.D4HRequestStatus;
import avila.schiatti.virdi.model.subscription.Subscription;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import avila.schiatti.virdi.resource.APIManager;
import avila.schiatti.virdi.resource.D4HRequestResource;
import avila.schiatti.virdi.resource.SubscriptionResource;
import avila.schiatti.virdi.resource.UserResource;
import avila.schiatti.virdi.service.request.D4HReqRequest;
import avila.schiatti.virdi.service.response.ErrorResponse;
import avila.schiatti.virdi.utils.JSONObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import unirest.HttpResponse;
import unirest.JsonNode;
import unirest.Unirest;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;
import xyz.morphia.query.Query;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class D4HRequestServiceTest {

    private static final StaticConfiguration config = StaticConfiguration.getInstance();
    private static final Integer PORT = 8888;
    private static final String TEST_WEB_APP_URL = "http://localhost:"+PORT+"/web/requests";
    private static final String TEST_API_APP_URL = "http://localhost:"+PORT+"/api/requests";
    private final static String MODELS_PACKAGE = "avila.schiatti.virdi.model";
    private final static String MONGODB_TEST_DATABASE = "test_data4help";

    // individual
    private static final String SSN = "testing_ssn_number";
    private static final String INDIVIDUAL_NAME = "John Doe";
    private static final String INDIVIDUAL_EMAIL = "my_personal_email@address.com";
    private static final String PASSWORD = "My Pa22w0rd";

    // third party
    private static final String COMPANY_EMAIL = "my_company@email.com";
    private static final String COMPANY_NAME = "my company name inc.";
    private static final String COMPANY_SECRET_KEY = "my_secret_key";

    private static Datastore datastore;
    private static APIManager apiManager;
    private static SubscriptionResource subscriptionResource;
    private static D4HRequestResource d4HRequestResource;

    private static void createDatastore(){
        Morphia morphia = new Morphia();
        morphia.mapPackage(MODELS_PACKAGE);
        MongoClientURI mongoClientURI = new MongoClientURI(config.getMongoDBConnectionString());
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        datastore = morphia.createDatastore(mongoClient, MONGODB_TEST_DATABASE);
    }

    private static UserResource setupUserResource() {
        return new UserResource(datastore);
    }

    private static SubscriptionResource setupSubscriptionResource() {
        subscriptionResource = new SubscriptionResource(datastore);
        return subscriptionResource;
    }

    private static D4HRequestResource setupD4HRequestResource() {
        d4HRequestResource = new D4HRequestResource(datastore);
        return d4HRequestResource;
    }

    private static Data4HelpApp app;

    @BeforeAll
    public static void beforeAll() {
        Unirest.config().setObjectMapper(new JSONObjectMapper());

        createDatastore();

        apiManager = mock(APIManager.class);
        D4HRequestService service = new D4HRequestService(setupD4HRequestResource(), setupSubscriptionResource(), setupUserResource(), apiManager);

        app = Data4HelpApp.getInstance();
        app.createServer(PORT)
                .registerService(service)
                .init();
    }

    @BeforeEach
    public void beforeEach(){
        reset(apiManager);
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
        // remove all created requests
        Query<D4HRequest> queryRequests = datastore.createQuery(D4HRequest.class);
        datastore.delete(queryRequests);
    }

    private Individual createAndStoreIndividual(){
        Individual i = new Individual();
        i.setSsn(SSN);
        i.setName(INDIVIDUAL_NAME);

        datastore.save(i);
        return i;
    }

    private ThirdParty createAndStoreThirdParty(){
        ThirdParty tp = new ThirdParty();
        tp.setName(COMPANY_NAME);
        tp.setSecretKey(COMPANY_SECRET_KEY);

        datastore.save(tp);
        return tp;
    }

    private D4HRequest createD4HRequest(){
        D4HRequest req = new D4HRequest();
        req.setThirdParty(createAndStoreThirdParty());
        req.setIndividual(createAndStoreIndividual());

        datastore.save(req);
        return req;
    }

    @Test
    @DisplayName("Test get all the requests of a valid individual")
    public void test_1(){
        D4HRequest request = createD4HRequest();

        HttpResponse<JsonNode> response = Unirest.get(TEST_WEB_APP_URL + "/")
                .header(Data4HelpApp.USER_ID, request.getIndividual().getId().toString())
                .asJson();

        JSONObject individual = response.getBody().getObject().getJSONArray("data").getJSONObject(0).getJSONObject("individual");
        JSONObject thirdParty = response.getBody().getObject().getJSONArray("data").getJSONObject(0).getJSONObject("thirdParty");
        String status = response.getBody().getObject().getJSONArray("data").getJSONObject(0).getString("status");

        assertEquals(INDIVIDUAL_NAME, individual.get("name"));
        assertEquals(COMPANY_NAME, thirdParty.get("name"));
        assertEquals(D4HRequestStatus.PENDING.toString(), status);
    }

    @Test
    @DisplayName("Test get all the requests of a valid third party")
    public void test_2(){
        D4HRequest req = new D4HRequest();
        req.setThirdParty(createAndStoreThirdParty());
        req.setIndividual(createAndStoreIndividual());
        req.setStatus(D4HRequestStatus.APPROVED);

        datastore.save(req);
        ///////////////////////

        HttpResponse<JsonNode> response = Unirest.get(TEST_WEB_APP_URL + "/")
                .header(Data4HelpApp.USER_ID, req.getThirdParty().getId().toString())
                .asJson();

        JSONObject individual = response.getBody().getObject().getJSONArray("data").getJSONObject(0).getJSONObject("individual");
        JSONObject thirdParty = response.getBody().getObject().getJSONArray("data").getJSONObject(0).getJSONObject("thirdParty");
        String status = response.getBody().getObject().getJSONArray("data").getJSONObject(0).getString("status");

        assertEquals(INDIVIDUAL_NAME, individual.get("name"));
        assertEquals(COMPANY_NAME, thirdParty.get("name"));
        assertEquals(D4HRequestStatus.APPROVED.toString(), status);
    }

    @Test
    @DisplayName("Test when not valid user id tries to get all its requests, and error is returned")
    public void test_3(){
        ObjectId fakeId = new ObjectId();
        ///////////////////////

        HttpResponse<ErrorResponse> response = Unirest.get(TEST_WEB_APP_URL + "/")
                .header(Data4HelpApp.USER_ID, fakeId.toString())
                .asObject(ErrorResponse.class);

        assertEquals(response.getBody().getMessage(), TrackMeError.NOT_VALID_USER.getMessage());
    }

    @Test
    @DisplayName("Test get all approved requests owned by a third party")
    public void test_4(){
        ThirdParty tp = createAndStoreThirdParty();
        D4HRequest req_1 = new D4HRequest();
        req_1.setThirdParty(tp);
        req_1.setIndividual(createAndStoreIndividual());
        req_1.setStatus(D4HRequestStatus.APPROVED);

        D4HRequest req_2 = new D4HRequest();
        req_2.setThirdParty(tp);
        req_2.setIndividual(createAndStoreIndividual());
        req_2.setStatus(D4HRequestStatus.PENDING);

        datastore.save(req_1);
        datastore.save(req_2);
        ///////////////////////

        HttpResponse<JsonNode> response = Unirest.get(TEST_WEB_APP_URL + "/")
                .queryString("status", "APPROVED")
                .header(Data4HelpApp.USER_ID, req_1.getThirdParty().getId().toString())
                .asJson();

        JSONObject individual = response.getBody().getObject().getJSONArray("data").getJSONObject(0).getJSONObject("individual");
        JSONObject thirdParty = response.getBody().getObject().getJSONArray("data").getJSONObject(0).getJSONObject("thirdParty");
        String status = response.getBody().getObject().getJSONArray("data").getJSONObject(0).getString("status");

        assertEquals(INDIVIDUAL_NAME, individual.get("name"));
        assertEquals(COMPANY_NAME, thirdParty.get("name"));
        assertEquals(D4HRequestStatus.APPROVED.toString(), status);
        assertEquals(1, response.getBody().getObject().getJSONArray("data").toList().size());
    }

    @Test
    @DisplayName("Test update a request from pending to rejected, should send one notification")
    public void test_5(){
        D4HRequest req = createD4HRequest();
        D4HReqRequest payload = new D4HReqRequest();
        payload.setStatus(D4HRequestStatus.REJECTED);
        ///////////////////////

        HttpResponse<JsonNode> response = Unirest.patch(TEST_WEB_APP_URL + "/{id}")
                .routeParam("id", req.getId().toString())
                .body(payload)
                .header(Data4HelpApp.USER_ID, req.getIndividual().getId().toString())
                .asJson();

        JSONObject individual = response.getBody().getObject().getJSONObject("data").getJSONObject("individual");
        JSONObject thirdParty = response.getBody().getObject().getJSONObject("data").getJSONObject("thirdParty");
        String status = response.getBody().getObject().getJSONObject("data").getString("status");

        assertEquals(INDIVIDUAL_NAME, individual.get("name"));
        assertEquals(COMPANY_NAME, thirdParty.get("name"));
        assertEquals(D4HRequestStatus.REJECTED.toString(), status);
        verify(apiManager, only()).sendNotification(any(ThirdParty.class), any(D4HRequest.class));
    }

    @Test
    @DisplayName("Test approve a request should create a subscription and send a notification")
    public void test_6(){
        D4HRequest req = createD4HRequest();
        D4HReqRequest payload = new D4HReqRequest();
        payload.setStatus(D4HRequestStatus.APPROVED);
        ///////////////////////

        String individualId = req.getIndividual().getId().toString();
        HttpResponse<JsonNode> response = Unirest.patch(TEST_WEB_APP_URL + "/{id}")
                .routeParam("id", req.getId().toString())
                .body(payload)
                .header(Data4HelpApp.USER_ID, individualId)
                .asJson();

        ArrayList<Subscription> subscriptions = (ArrayList<Subscription>) subscriptionResource.getAllByIndividual(req.getIndividual());

        JSONObject individual = response.getBody().getObject().getJSONObject("data").getJSONObject("individual");
        JSONObject thirdParty = response.getBody().getObject().getJSONObject("data").getJSONObject("thirdParty");
        String status = response.getBody().getObject().getJSONObject("data").getString("status");

        D4HRequest request = d4HRequestResource.getById(req.getId());

        assertEquals(request.getIndividual().getName(), individual.get("name"));
        assertEquals(request.getThirdParty().getName(), thirdParty.get("name"));
        assertEquals(request.getStatus().toString(), status);

        verify(apiManager).sendNotification(any(ThirdParty.class), any(D4HRequest.class));

        assertEquals(1, subscriptions.size());
        assertEquals(req.getThirdParty().getId().toString(), subscriptions.get(0).getThirdParty().getId().toString());
    }

    @Test
    @DisplayName("Test try to update a not valid request id, should return an error")
    public void test_7(){
        ObjectId fakeId = new ObjectId();
        D4HRequest req = createD4HRequest();
        D4HReqRequest payload = new D4HReqRequest();
        ///////////////////////

        String individualId = req.getIndividual().getId().toString();
        HttpResponse<ErrorResponse> response = Unirest.patch(TEST_WEB_APP_URL + "/{id}")
                .routeParam("id", fakeId.toString())
                .body(payload)
                .header(Data4HelpApp.USER_ID, individualId)
                .asObject(ErrorResponse.class);

        assertEquals(TrackMeError.NOT_VALID_REQUEST_ID.getMessage(), response.getBody().getMessage());
        verify(apiManager, never()).sendNotification(any(ThirdParty.class), any(D4HRequest.class));
    }

    @Test
    @DisplayName("Test not valid user id tries to update a request, should return an error")
    public void test_8(){
        ObjectId fakeId = new ObjectId();
        D4HRequest req = createD4HRequest();
        D4HReqRequest payload = new D4HReqRequest();
        ///////////////////////

        HttpResponse<ErrorResponse> response = Unirest.patch(TEST_WEB_APP_URL + "/{id}")
                .routeParam("id", req.getId().toString())
                .body(payload)
                .header(Data4HelpApp.USER_ID, fakeId.toString())
                .asObject(ErrorResponse.class);

        assertEquals(TrackMeError.NOT_VALID_USER.getMessage(), response.getBody().getMessage());
        verify(apiManager, never()).sendNotification(any(ThirdParty.class), any(D4HRequest.class));
    }

    @Test
    @DisplayName("Test api deletes a request, should remove it from the data base")
    public void test_9(){
        D4HRequest req = createD4HRequest();
        ///////////////////////

        HttpResponse<JsonNode> response = Unirest.delete(TEST_API_APP_URL + "/{id}")
                .routeParam("id", req.getId().toString())
                .header(Data4HelpApp.SECRET_KEY, COMPANY_SECRET_KEY)
                .asJson();

        D4HRequest nullRequest = d4HRequestResource.getById(req.getId());
        String removedId = response.getBody().getObject().getString("data");

        assertEquals(req.getId().toString(), removedId);
        assertNull(nullRequest);
    }

    @Test
    @DisplayName("Test api remove a not valid request should return an error")
    public void test_10(){
        ObjectId fakeId = new ObjectId();
        ///////////////////////

        HttpResponse<ErrorResponse> response = Unirest.delete(TEST_API_APP_URL + "/{id}")
                .routeParam("id", fakeId.toString())
                .header(Data4HelpApp.SECRET_KEY, COMPANY_SECRET_KEY)
                .asObject(ErrorResponse.class);

        assertEquals(TrackMeError.NOT_VALID_REQUEST_ID.getMessage(), response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test api not valid third party tries to remove a request should return an error")
    public void test_11(){
        D4HRequest req = createD4HRequest();
        ///////////////////////

        HttpResponse<ErrorResponse> response = Unirest.delete(TEST_API_APP_URL + "/{id}")
                .routeParam("id", req.getId().toString())
                .header(Data4HelpApp.SECRET_KEY, "not_valid_secret_key")
                .asObject(ErrorResponse.class);

        assertEquals(TrackMeError.NOT_VALID_SECRET_KEY.getMessage(), response.getBody().getMessage());

        D4HRequest request = d4HRequestResource.getById(req.getId());
        assertNotNull(request);
        assertEquals(req.getId().toString(), request.getId().toString());
    }

    @Test
    @DisplayName("Test api a third party tries to delete a request from another third party, should return an error")
    public void test_12(){
        D4HRequest req = createD4HRequest();
        ThirdParty tp_2 = new ThirdParty();
        tp_2.setSecretKey("some_other_secret_key");
        D4HRequest req_2 = new D4HRequest();
        req_2.setThirdParty(tp_2);

        datastore.save(tp_2);
        datastore.save(req_2);
        ///////////////////////

        HttpResponse<ErrorResponse> response = Unirest.delete(TEST_API_APP_URL + "/{id}")
                .routeParam("id", req_2.getId().toString())
                .header(Data4HelpApp.SECRET_KEY, COMPANY_SECRET_KEY)
                .asObject(ErrorResponse.class);

        assertEquals(TrackMeError.NOT_VALID_SECRET_KEY.getMessage(), response.getBody().getMessage());

        D4HRequest request_1 = d4HRequestResource.getById(req.getId());
        assertNotNull(request_1);
        assertEquals(req.getId().toString(), request_1.getId().toString());

        D4HRequest request_2 = d4HRequestResource.getById(req_2.getId());
        assertNotNull(request_2);
        assertEquals(req_2.getId().toString(), request_2.getId().toString());
    }

    @Test
    @DisplayName("Test api a valid third party creates a request for a valid ssn, should store a new request for the individual")
    public void test_13(){
        createAndStoreIndividual();
        createAndStoreThirdParty();
        ///////////////

        D4HReqRequest body = new D4HReqRequest();
        body.setSsn(SSN);

        HttpResponse<JsonNode> response = Unirest.post(TEST_API_APP_URL + "/")
                .body(body)
                .header(Data4HelpApp.SECRET_KEY, COMPANY_SECRET_KEY)
                .asJson();

        JSONObject individual = response.getBody().getObject().getJSONObject("data").getJSONObject("individual");
        JSONObject thirdParty = response.getBody().getObject().getJSONObject("data").getJSONObject("thirdParty");
        String id = response.getBody().getObject().getJSONObject("data").getString("id");

        // get the saved request.
        D4HRequest request = d4HRequestResource.getById(id);

        assertNotNull(request);
        assertEquals(request.getIndividual().getName(), individual.get("name"));
        assertEquals(request.getThirdParty().getName(), thirdParty.get("name"));
        assertEquals(D4HRequestStatus.PENDING.toString(), request.getStatus().toString());
    }

}
