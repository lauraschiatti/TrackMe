package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
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
     * @param datastore
     */
    public D4HRequestResource(Datastore datastore) {
        super(datastore, D4HRequest.class);
    }

    private D4HRequestResource() {
        super(D4HRequest.class);
    }

    public static D4HRequestResource create(){
        return new D4HRequestResource();
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

    public Long countByUserId(ObjectId uid, D4HRequestStatus status){
        Query<D4HRequest> query = datastore.find(D4HRequest.class);
        query.or(
                query.criteria("individual").equal(uid),
                query.criteria("thirdParty").equal(uid)
        );
        query.field("status").equal(status);
        return query.count();
    }

    public Collection<D4HRequest> getByUserId(String userId, D4HRequestStatus status){
        if(status == null){
            return getByUserId(userId);
        }

        return datastore.find(D4HRequest.class)
                .field("individual")
                .equal(new ObjectId(userId))
                .field("status")
                .equal(status)
                .asList();
    }

    public D4HRequest getByUserIdAndThirdPartyId(ObjectId userId, ObjectId thirdPartyId){
        return datastore.find(D4HRequest.class)
                .field("individual")
                .equal(userId)
                .field("thirdParty")
                .equal(thirdPartyId)
                .get();
    }

    public D4HRequest checkApprovedRequest(ObjectId userId, ObjectId thirdPartyId){
        D4HRequest request = getByUserIdAndThirdPartyId(userId, thirdPartyId);

        if(request == null){
            throw new TrackMeException(TrackMeError.NO_REQUEST_FOUND);
        } else if( !D4HRequestStatus.APPROVED.equals(request.getStatus())){
            throw new TrackMeException(TrackMeError.NO_APPROVED_REQUEST);
        }

        return request;
    }

    public Collection<D4HRequest> getByThirdPartyId(String thirdPartyId, D4HRequestStatus status){
        Query<D4HRequest> query = datastore.find(D4HRequest.class)
                .field("thirdParty")
                .equal(new ObjectId(thirdPartyId));

        if(status != null){
            query.field("status")
                    .equal(status);
        }

        return query.asList();
    }
}
