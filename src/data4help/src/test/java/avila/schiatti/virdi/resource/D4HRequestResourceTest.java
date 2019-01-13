package avila.schiatti.virdi.resource;


import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.request.D4HRequestStatus;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;
import xyz.morphia.query.Query;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class D4HRequestResourceTest {

    private final static String MODELS_PACKAGE = "avila.schiatti.virdi.model";
    private final static String MONGODB_TEST_DATABASE = "test_data4help";
    private static final String SSN = "testing_ssn_number";
    private static final String INDIVIDUAL_NAME = "John Doe";
    private static final String COMPANY_NAME = "My Company Name Inc.";
    private static final String INDIVIDUAL_EMAIL = "my_personal_email@address.com";
    private static final String COMPANY_EMAIL = "my_company_email@address.com";
    private static final String PASSWORD = "My Pa22w0rd";

    private final static Morphia morphia = new Morphia();
    private final static StaticConfiguration config = StaticConfiguration.getInstance();

    private static D4HRequestResource resource;
    private static Datastore datastore;

    private static Datastore createDatastore(){
        morphia.mapPackage(MODELS_PACKAGE);
        MongoClientURI mongoClientURI = new MongoClientURI(config.getMongoDBConnectionString());
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        return morphia.createDatastore(mongoClient, MONGODB_TEST_DATABASE);
    }

    @BeforeAll
    public static void before() {
        datastore = createDatastore();
        resource = new D4HRequestResource(datastore);
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

    @Test
    @DisplayName("Test when id is valid, getById should return a request")
    public void test_1(){
        D4HRequest original = createD4HRequest();

        D4HRequest req = resource.getById(original.getId());

        assertEquals(original.getId(), req.getId());
    }

    @Test
    @DisplayName("Test when id is not valid, getById should return null")
    public void test_2(){
        ObjectId fakeId = new ObjectId();
        D4HRequest req = resource.getById(fakeId);

        assertNull(req);
    }

    @Test
    @DisplayName("Test when update a request, it should be saved into the db")
    public void test_3(){
        D4HRequest req = createD4HRequest();
        req.setStatus(D4HRequestStatus.REJECTED);
        resource.update(req);

        D4HRequest updatedReq = resource.getById(req.getId());

        assertEquals(updatedReq.getStatus(), D4HRequestStatus.REJECTED);
    }

    @Test
    @DisplayName("Test when add a request, it should be saved into the db")
    public void test_4(){
        D4HRequest req = new D4HRequest();
        req.setThirdParty(createAndStoreThirdParty());
        req.setIndividual(createAndStoreIndividual());
        req.setStatus(D4HRequestStatus.APPROVED);

        resource.add(req);

        D4HRequest updatedReq = resource.getById(req.getId());

        assertEquals(updatedReq.getStatus(), D4HRequestStatus.APPROVED);
    }

    @Test
    @DisplayName("Test when remove a request by id, it should be removed from the db")
    public void test_5(){
        D4HRequest req = createD4HRequest();

        resource.removeById(req.getId());

        D4HRequest updatedReq = resource.getById(req.getId().toString());

        assertNull(updatedReq);
    }

    @Test
    @DisplayName("Test when accepting a request, it should have an APPROVED status")
    public void test_6(){
        D4HRequest req = createD4HRequest();

        resource.accept(req);

        D4HRequest updatedReq = resource.getById(req.getId().toString());

        assertEquals(updatedReq.getStatus(), D4HRequestStatus.APPROVED);
    }

    @Test
    @DisplayName("Test when rejecting a request, it should have an REJECTED status")
    public void test_7(){
        D4HRequest req = createD4HRequest();

        resource.reject(req);

        D4HRequest updatedReq = resource.getById(req.getId().toString());

        assertEquals(updatedReq.getStatus(), D4HRequestStatus.REJECTED);
    }

    @Test
    @DisplayName("Test when getting all the request from a valid user, it should return all the stored requests for the user")
    public void test_8(){
        Individual i = createAndStoreIndividual();
        D4HRequest req_1 = new D4HRequest();
        req_1.setIndividual(i);
        datastore.save(req_1);

        D4HRequest req_2 = new D4HRequest();
        req_2.setIndividual(i);
        datastore.save(req_2);

        ArrayList<D4HRequest> requests = (ArrayList<D4HRequest>) resource.getByUserId(i.getId().toString());

        assertNotNull(requests);
        assertEquals(requests.size(), 2);

        for(int index = 0; index < requests.size(); index++){
            assertEquals(requests.get(index).getIndividual().getId(), i.getId());
        }
    }

    @Test
    @DisplayName("Test when getting all the request from a third party, it should return all the stored requests for the third party")
    public void test_9(){
        ThirdParty tp = createAndStoreThirdParty();
        D4HRequest req_1 = new D4HRequest();
        req_1.setThirdParty(tp);
        datastore.save(req_1);

        D4HRequest req_2 = new D4HRequest();
        req_2.setThirdParty(tp);
        datastore.save(req_2);

        ArrayList<D4HRequest> requests = (ArrayList<D4HRequest>) resource.getByThirdPartyId(tp.getId().toString(), null);

        assertNotNull(requests);
        assertEquals(requests.size(), 2);

        for(int index = 0; index < requests.size(); index++){
            assertEquals(requests.get(index).getThirdParty().getId(), tp.getId());
        }
    }

    @Test
    @DisplayName("Test when getting all the request with a particular status from a third party, it should return the stored requests with that status from that third party")
    public void test_10(){
        ThirdParty tp = createAndStoreThirdParty();
        D4HRequest req_1 = new D4HRequest();
        req_1.setThirdParty(tp);
        req_1.setStatus(D4HRequestStatus.REJECTED);
        datastore.save(req_1);

        D4HRequest req_2 = new D4HRequest();
        req_2.setThirdParty(tp);
        datastore.save(req_2);

        ArrayList<D4HRequest> requests = (ArrayList<D4HRequest>) resource.getByThirdPartyId(tp.getId().toString(), D4HRequestStatus.REJECTED);

        assertNotNull(requests);
        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getThirdParty().getId(), tp.getId());
        assertEquals(requests.get(0).getStatus(), D4HRequestStatus.REJECTED);
    }

    @Test
    @DisplayName("Test when getting all the request from a not valid user, it should return an empty collection")
    public void test_11(){
        ObjectId fakeId = new ObjectId();
        ArrayList<D4HRequest> requests = (ArrayList<D4HRequest>) resource.getByUserId(fakeId.toString());

        assertNotNull(requests);
        assertEquals(requests.size(),0);
    }

    @Test
    @DisplayName("Test when getting all the request from a not valid third party, it should return an empty collection")
    public void test_12(){
        ObjectId fakeId = new ObjectId();
        ArrayList<D4HRequest> requests = (ArrayList<D4HRequest>) resource.getByThirdPartyId(fakeId.toString(), null);

        assertNotNull(requests);
        assertEquals(requests.size(),0);
    }

    @Test
    @DisplayName("Test when getting all the request from a not valid third party, it should return an empty collection")
    public void test_13(){
        ObjectId fakeId = new ObjectId();
        D4HRequest req = new D4HRequest();
        req.setThirdParty(createAndStoreThirdParty());
        datastore.save(req);

        ArrayList<D4HRequest> requests = (ArrayList<D4HRequest>) resource.getByThirdPartyId(fakeId.toString(), D4HRequestStatus.PENDING);

        assertNotNull(requests);
        assertEquals(requests.size(), 0);
    }

    @Test
    @DisplayName("Test when a request from a valid user and a valid third party, it should return the request")
    public void test_14(){
        try {
            ThirdParty tp = createAndStoreThirdParty();
            Individual i = createAndStoreIndividual();
            D4HRequest req_1 = new D4HRequest();
            req_1.setThirdParty(tp);
            req_1.setIndividual(i);
            datastore.save(req_1);

            D4HRequest request = resource.getByUserIdAndThirdPartyId(i.getId(), tp.getId());
            assertEquals(request.getIndividual().getId(), i.getId());
            assertEquals(request.getThirdParty().getId(), tp.getId());
        }catch(Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Test when a request from a not valid user and a valid third party, it should return null")
    public void test_15(){
        try {
            ObjectId fakeId = new ObjectId();
            ThirdParty tp = createAndStoreThirdParty();
            D4HRequest req_1 = new D4HRequest();
            req_1.setThirdParty(tp);
            req_1.setIndividual(createAndStoreIndividual());
            datastore.save(req_1);

            resource.checkApprovedRequest(fakeId, tp.getId());

            fail();
        }catch(TrackMeException ex){
            assertEquals(TrackMeError.NO_REQUEST_FOUND.getMessage(), ex.getMessage());
        }
    }

    @Test
    @DisplayName("Test when a request from a valid user and a not valid third party, it should return null")
    public void test_16(){
        try {
            ObjectId fakeId = new ObjectId();
            Individual i = createAndStoreIndividual();
            D4HRequest req_1 = new D4HRequest();
            req_1.setThirdParty(createAndStoreThirdParty());
            req_1.setIndividual(i);
            datastore.save(req_1);

            resource.checkApprovedRequest(i.getId(), fakeId);
            fail();
        }catch(TrackMeException ex){
            assertEquals(TrackMeError.NO_REQUEST_FOUND.getMessage(), ex.getMessage());
        }
    }


    private Individual createAndStoreIndividual(){
        Individual i = new Individual();
        i.setSsn(SSN);
        i.setName(INDIVIDUAL_NAME);
        i.setEmail(INDIVIDUAL_EMAIL);
        i.setPassword(AuthenticationManager.hashPassword(PASSWORD));

        datastore.save(i);
        return i;
    }

    private ThirdParty createAndStoreThirdParty(){
        ThirdParty tp = new ThirdParty();
        tp.setName(COMPANY_NAME);
        tp.setEmail(COMPANY_EMAIL);
        tp.setPassword(AuthenticationManager.hashPassword(PASSWORD));

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
}
