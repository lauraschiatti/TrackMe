package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;
import xyz.morphia.query.Query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

public class UserResourceTest {
    private final static String MODELS_PACKAGE = "avila.schiatti.virdi.model";
    private final static String MONGODB_TEST_DATABASE = "test_data4help";
    private static final String SSN = "testing_ssn_number";
    private static final String INDIVIDUAL_NAME = "John Doe";
    private static final String COMPANY_NAME = "My Company Name Inc.";
    private static final String INDIVIDUAL_EMAIL = "my_personal_email@address.com";
    private static final String COMPANY_EMAIL = "my_company_email@address.com";
    private static final String PASSWORD = "My Pa22w0rd";
    private static final String HASHED_PASSWORD = AuthenticationManager.hashPassword(PASSWORD);

    private final static Morphia morphia = new Morphia();
    private final static StaticConfiguration config = StaticConfiguration.getInstance();

    private static UserResource resource;
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
        resource = new UserResource(datastore);
    }

    @AfterEach
    public void afterEach(){
        // remove all created users.
        Query<D4HUser> query = datastore.createQuery(D4HUser.class);
        datastore.delete(query);
    }

    @Test
    @DisplayName("Test if getByEmailAndPass returns a previously saved Individual")
    public void testIfGetUserByEmailAndPassRetrievesIndividual(){
        createIndividualUser();

        Individual user = (Individual) resource.getByEmailAndPass(INDIVIDUAL_EMAIL, HASHED_PASSWORD);

        assertEquals(user.getEmail(), INDIVIDUAL_EMAIL);
        assertEquals(user.getPassword(), HASHED_PASSWORD);
        assertEquals(user.getSsn(), SSN);
        assertEquals(user.getName(), INDIVIDUAL_NAME);
    }

    @Test
    @DisplayName("Test if getByEmailAndPass returns a previously saved ThirdParty")
    public void testIfGetUserByEmailAndPassRetrievesThirdParty(){
        createThirdPartyUser();

        ThirdParty user = (ThirdParty) resource.getByEmailAndPass(COMPANY_EMAIL, HASHED_PASSWORD);

        assertEquals(user.getEmail(), COMPANY_EMAIL);
        assertEquals(user.getPassword(), HASHED_PASSWORD);
        assertEquals(user.getName(), COMPANY_NAME);
    }

    @Test
    @DisplayName("Test if add saves a user in the database")
    public void testIfAddMethodSavesUser(){
        Individual i = new Individual();
        i.setSsn(SSN);
        i.setEmail(INDIVIDUAL_EMAIL);

        ThirdParty tp = new ThirdParty();
        tp.setName(COMPANY_NAME);
        tp.setEmail(COMPANY_EMAIL);
        tp.setPassword(AuthenticationManager.hashPassword(PASSWORD));

        resource.add(i);
        resource.add(tp);

        Individual individual = datastore.find(Individual.class).field("email").equal(INDIVIDUAL_EMAIL).get();
        assertEquals(individual.getSsn(), SSN);
        assertEquals(individual.getId(), i.getId());

        ThirdParty thirdParty = datastore.find(ThirdParty.class).field("email").equal(COMPANY_EMAIL).get();
        assertEquals(thirdParty.getName(), COMPANY_NAME);
        assertEquals(thirdParty.getId(), tp.getId());
    }

    @Test
    @DisplayName("Test if getByEmailAndPass returns null when the user is not found")
    public void testIfGetByEmailAndPassReturnsNullWhenNotValidPassword(){
        D4HUser user = resource.getByEmailAndPass(INDIVIDUAL_EMAIL, "Not valid password");
        assertNull(user);
    }

    private void createIndividualUser(){
        Individual i = new Individual();
        i.setSsn(SSN);
        i.setName(INDIVIDUAL_NAME);
        i.setEmail(INDIVIDUAL_EMAIL);
        i.setPassword(AuthenticationManager.hashPassword(PASSWORD));

        datastore.save(i);
    }

    private void createThirdPartyUser(){
        ThirdParty tp = new ThirdParty();
        tp.setName(COMPANY_NAME);
        tp.setEmail(COMPANY_EMAIL);
        tp.setPassword(AuthenticationManager.hashPassword(PASSWORD));

        datastore.save(tp);
    }
}
