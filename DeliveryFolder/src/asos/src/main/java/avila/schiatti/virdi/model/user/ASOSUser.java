package avila.schiatti.virdi.model.user;

import avila.schiatti.virdi.model.data.BloodType;
import avila.schiatti.virdi.model.data.Gender;
import avila.schiatti.virdi.model.health.Status;
import org.bson.types.ObjectId;
import xyz.morphia.annotations.*;

import java.time.LocalDate;

@Entity("user")
public class ASOSUser {
    @Id
    private ObjectId id;

    @Indexed(options = @IndexOptions(unique = true))
    private String ssn;

    @Reference(idOnly = true)
    private Address address;
    private String name;
    @Embedded
    private Status status;
    @Embedded
    private BloodType bloodType;
    @Embedded
    private Gender gender;
    private LocalDate birthDate;

    @Reference(idOnly = true)
    private EmergencyContact contact;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public EmergencyContact getContact() {
        return contact;
    }

    public void setContact(EmergencyContact contact) {
        this.contact = contact;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
