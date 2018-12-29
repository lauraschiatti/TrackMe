package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import org.bson.types.ObjectId;
import xyz.morphia.Datastore;

public class UserResource extends Resource<D4HUser> {

    /**
     * Only for testing method
     * @param dbManager
     * @param datastore
     */
    public UserResource(DBManager dbManager, Datastore datastore){
        super(dbManager, datastore);
    }

    private UserResource(){
        super();
    }

    public static UserResource create(){
        return new UserResource();
    }

    public D4HUser getByEmailAndPass(String email, String password){
        return this.datastore
                .find(D4HUser.class)
                .field("email")
                .equal(email)
                .field("password")
                .equal(password)
                .get();
    }

    @Override
    public void add(D4HUser o) {
        this.datastore.save(o);
    }

    @Override
    public D4HUser getById(String id) {
        return getById(new ObjectId(id));
    }

    @Override
    public D4HUser getById(ObjectId id) {
        return datastore.find(D4HUser.class)
                .field("id")
                .equal(id)
                .get();
    }

    public ThirdParty getThirdPartyBySecretKey(String secretKey){
        return this.datastore.find(ThirdParty.class)
                .field("secretKey")
                .equal(secretKey)
                .get();
    }

    public Individual getBySSN(String ssn){
        return this.datastore.find(Individual.class)
                .field("ssn")
                .equal(ssn)
                .get();
    }
}
