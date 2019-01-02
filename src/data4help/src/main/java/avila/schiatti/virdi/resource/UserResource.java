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
        super(dbManager, datastore, D4HUser.class);
    }

    private UserResource(){
        super(D4HUser.class);
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
