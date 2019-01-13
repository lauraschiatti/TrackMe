package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.user.Address;
import avila.schiatti.virdi.model.user.EmergencyContact;
import xyz.morphia.Datastore;

public class EmergencyContactResource extends Resource<EmergencyContact> {
    public EmergencyContactResource(Datastore datastore) {
        super(datastore, EmergencyContact.class);
    }

    private EmergencyContactResource() {
        super(EmergencyContact.class);
    }

    public static EmergencyContactResource create(){
        return new EmergencyContactResource();
    }

    public EmergencyContact getByAddress(Address address){
        return datastore.find(EmergencyContact.class)
                .field("address")
                .equal(address)
                .get();
    }
}
