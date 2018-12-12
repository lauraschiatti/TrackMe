package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.request.D4HRequestStatus;
import org.bson.types.ObjectId;
import xyz.morphia.Datastore;

import java.util.Collection;

public class D4HRequestResource extends Resource<D4HRequest> {

    /**
     * Only for testing
     * @param dbManager
     * @param datastore
     */
    public D4HRequestResource(DBManager dbManager, Datastore datastore) {
        super(dbManager, datastore);
    }

    private D4HRequestResource() {
        super();
    }

    public static D4HRequestResource create(){
        return new D4HRequestResource();
    }

    @Override
    public void update(D4HRequest o) {
        datastore.save(o);
    }

    @Override
    public void add(D4HRequest o) {
        this.datastore.save(o);
    }

    public Collection<D4HRequest> getByUserId(String userId){
        return this.datastore.find(D4HRequest.class)
                .field("individual")
                .equal(new ObjectId(userId))
                .asList();
    }

    public Collection<D4HRequest> getByUserId(String userId, D4HRequestStatus status){
        return this.datastore.find(D4HRequest.class)
                .field("individual")
                .equal(new ObjectId(userId))
                .field("status")
                .equal(status)
                .asList();
    }

    public D4HRequest getByUserIdAndThirdPartyId(String userId, String thirdPartyId){
        return this.datastore.find(D4HRequest.class)
                .field("individual")
                .equal(new ObjectId(userId))
                .field("thirdParty")
                .equal(new ObjectId(thirdPartyId))
                .get();
    }

    public Collection<D4HRequest> getByThirdPartyId(String thirdPartyId, D4HRequestStatus status){
        return this.datastore.find(D4HRequest.class)
                .field("thirdParty")
                .equal(new ObjectId(thirdPartyId))
                .field("status")
                .equal(status)
                .asList();
    }
}
