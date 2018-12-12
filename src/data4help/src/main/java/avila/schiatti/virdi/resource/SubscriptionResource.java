package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.model.subscription.Subscription;
import org.bson.types.ObjectId;
import xyz.morphia.Datastore;
import xyz.morphia.query.Query;

public class SubscriptionResource extends Resource<Subscription> {

    /**
     * Only for testing
     * @param dbManager
     */
    public SubscriptionResource(DBManager dbManager, Datastore datastore) {
        super(dbManager, datastore);
    }

    private SubscriptionResource() {
        super();
    }

    public static SubscriptionResource create(){
        return new SubscriptionResource();
    }

    @Override
    public void add(Subscription o) {
        this.datastore.save(o);
    }

    @Override
    public void removeById(String id) {
        Query<Subscription> q = datastore.createQuery(Subscription.class)
                .filter("id", new ObjectId(id));
        this.datastore.delete(q);
    }

    @Override
    public void remove(Subscription o) {
        this.datastore.delete(o);
    }
}
