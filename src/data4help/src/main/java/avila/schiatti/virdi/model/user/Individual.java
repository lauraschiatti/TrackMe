package avila.schiatti.virdi.model.user;

import avila.schiatti.virdi.model.data.Address;
import avila.schiatti.virdi.model.data.BloodType;
import avila.schiatti.virdi.model.data.Data;
import avila.schiatti.virdi.model.data.Gender;
import xyz.morphia.annotations.*;

import java.time.LocalDate;

@Entity("user")
public class Individual extends D4HUser {
    private String name;
    @Indexed(options = @IndexOptions(partialFilter = "{ ssn : { $exists : true } }", unique = true))
    private String ssn;
    private Float weight;
    private Float height;
    private LocalDate birthDate;
    @Embedded
    private Gender gender;
    @Embedded
    private Address address;
    @Embedded
    private BloodType bloodType;

    @Override
    public D4HUserRole getRole() {
        return D4HUserRole.INDIVIDUAL;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
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

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }
}
