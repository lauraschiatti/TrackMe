package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.user.ASOSUser;
import xyz.morphia.Datastore;

public class UserResource extends Resource<ASOSUser> {

    /**
     * Only for testing method
     *
     * @param datastore
     */
    public UserResource(Datastore datastore) {
        super(datastore, ASOSUser.class);
    }

    private UserResource() {
        super(ASOSUser.class);
    }

    public static UserResource create() {
        return new UserResource();
    }

    public ASOSUser getBySSN(String ssn){
        return this.datastore.find(ASOSUser.class)
                .field("ssn")
                .equal(ssn)
                .get();
    }
}
