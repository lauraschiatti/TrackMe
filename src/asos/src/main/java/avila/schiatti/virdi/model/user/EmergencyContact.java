package avila.schiatti.virdi.model.user;

import org.bson.types.ObjectId;
import xyz.morphia.annotations.*;

@Entity("emergency_contact")
public class EmergencyContact {
    @Id
    private ObjectId id;

    @Indexed
    private String email;
    private String name;
    private String phone;
    private String url;

    @Reference(idOnly = true)
    private Address address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
}
