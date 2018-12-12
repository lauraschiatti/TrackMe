package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.request.D4HRequestStatus;
import org.bson.types.ObjectId;
import xyz.morphia.Datastore;
import xyz.morphia.query.Query;
import xyz.morphia.query.UpdateOperations;

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
    public D4HRequest getById(String id) {
        return this.datastore
                .find(D4HRequest.class)
                .field("id")
                .equal(new ObjectId(id))
                .get();
    }

    @Override
    public void update(D4HRequest o) {
        datastore.save(o);
    }

    @Override
    public void add(D4HRequest o) {
        this.datastore.save(o);
    }

    @Override
    public void removeById(String id) {
        this.removeById(new ObjectId(id));
    }

    @Override
    public void removeById(ObjectId id) {
        Query<D4HRequest> query = this.datastore.createQuery(D4HRequest.class).field("id").equal(id);
        this.datastore.delete(query);
    }

    public D4HRequest reject(D4HRequest r){
        r.setStatus(D4HRequestStatus.REJECTED);
        Query<D4HRequest> query = this.datastore.createQuery(D4HRequest.class);
        query.field("id")
                .equal(r.getId());

        UpdateOperations<D4HRequest> opts = this.datastore.createUpdateOperations(D4HRequest.class);
        opts.set("status", D4HRequestStatus.REJECTED);

        this.datastore.update(query, opts);
        return r;
    }

    public D4HRequest accept(D4HRequest r){
        r.setStatus(D4HRequestStatus.APPROVED);
        Query<D4HRequest> query = this.datastore.createQuery(D4HRequest.class);
        query.field("id")
                .equal(r.getId());

        UpdateOperations<D4HRequest> opts = this.datastore.createUpdateOperations(D4HRequest.class);
        opts.set("status", D4HRequestStatus.APPROVED);

        this.datastore.update(query, opts);
        return r;
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
