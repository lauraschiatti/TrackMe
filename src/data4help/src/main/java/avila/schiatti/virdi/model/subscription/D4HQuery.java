package avila.schiatti.virdi.model.subscription;

import avila.schiatti.virdi.model.data.Address;
import avila.schiatti.virdi.model.data.BloodType;
import avila.schiatti.virdi.model.data.Gender;
import avila.schiatti.virdi.model.user.Individual;
import xyz.morphia.annotations.*;

@Embedded
@Indexes(@Index(fields = { @Field("individual") }))
public class D4HQuery {
    @Reference(idOnly = true)
    private Individual individual;
    private String country;
    private String city;
    private String province;
    @Embedded
    private Gender gender;
    private Integer minAge;
    private Integer maxAge;
    @Embedded
    private BloodType bloodType;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public void setAddress(Address address){
        this.country = address.getCountry();
        this.province = address.getProvince();
        this.city = address.getCity();
    }
}
