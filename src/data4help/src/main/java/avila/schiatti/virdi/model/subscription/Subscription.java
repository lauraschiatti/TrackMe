package avila.schiatti.virdi.model.subscription;

import avila.schiatti.virdi.model.user.ThirdParty;
import org.bson.types.ObjectId;
import xyz.morphia.annotations.Embedded;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.Reference;

@Entity("subscription")
public class Subscription {
    @Id
    private ObjectId id;

    @Reference(idOnly = true)
    private ThirdParty thirdParty;
    @Embedded
    private D4HQuery filter;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ThirdParty getThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(ThirdParty thirdParty) {
        this.thirdParty = thirdParty;
    }

    public D4HQuery getFilter() {
        return filter;
    }

    public void setFilter(D4HQuery filter) {
        this.filter = filter;
    }
}
