package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.request.D4HRequest;
import avila.schiatti.virdi.model.subscription.Subscription;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import org.bson.types.ObjectId;
import xyz.morphia.Datastore;
import xyz.morphia.query.Query;

import java.time.LocalDateTime;
import java.util.Collection;

public class SubscriptionResource extends Resource<Subscription> {

    /**
     * Only for testing
     */
    public SubscriptionResource(Datastore datastore) {
        super(datastore, Subscription.class);
    }

    private SubscriptionResource() {
        super(Subscription.class);
    }

    public static SubscriptionResource create(){
        return new SubscriptionResource();
    }

    public Collection<Subscription> getAllByIndividual(Individual individual){
        return datastore.find(Subscription.class)
                .field("filter.individual")
                .equal(individual)
                .asList();
    }

    public Collection<Subscription> getAllByOwner(ThirdParty thirdParty){
        return datastore.find(Subscription.class)
                .field("thirdParty")
                .equal(thirdParty)
                .asList();
    }

    public Subscription getByOwnerAndId(String thirdPartyId, String sid){
        return datastore.find(Subscription.class)
                .field("id")
                .equal(new ObjectId(sid))
                .field("thirdParty")
                .equal(new ObjectId(thirdPartyId))
                .get();
    }

    public Collection<Subscription> getToBeExecutedSubscriptions() {
        Query<Subscription> query = datastore.find(Subscription.class)
                .field("filter.individual")
                .doesNotExist()
                .filter("nextExecution <", LocalDateTime.now());

        return query.asList();
    }

    public void removeByRequest(D4HRequest req) {
        Query<Subscription> query = datastore.createQuery(Subscription.class)
                .field("request")
                .equal(req);

        datastore.delete(query);
    }
}
