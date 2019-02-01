package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.user.Address;
import xyz.morphia.Datastore;

public class AddressResource extends Resource<Address> {
    public AddressResource(Datastore datastore) {
        super(datastore, Address.class);
    }

    public AddressResource() {
        super(Address.class);
    }

    public static AddressResource create(){
        return new AddressResource();
    }

    public Address get(Address address){
        return datastore.find(Address.class)
                .field("country")
                .equal(address.getCountry())
                .field("province")
                .equal(address.getProvince())
                .field("city")
                .equal(address.getCity())
                .get();
    }
}
