package avila.schiatti.virdi.model.user;

import avila.schiatti.virdi.model.data.Gender;
import avila.schiatti.virdi.model.data.Address;
import avila.schiatti.virdi.model.data.BloodType;
import avila.schiatti.virdi.model.data.Data;
import xyz.morphia.annotations.Embedded;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Reference;

import java.util.Date;

@Entity("user")
public class Individual extends D4HUser {
    private String name;
    private String ssn;
    private Date birthDate;
    @Embedded
    private Gender gender;
    @Embedded
    private Address address;
    @Embedded
    private BloodType bloodType;
    @Reference(idOnly = true)
    @Embedded
    private Data data;

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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Boolean hasSsn(){
        return ssn != null;
    }
}
