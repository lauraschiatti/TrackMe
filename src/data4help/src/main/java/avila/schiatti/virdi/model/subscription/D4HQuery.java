package avila.schiatti.virdi.model.subscription;

import avila.schiatti.virdi.model.data.BloodType;
import avila.schiatti.virdi.model.data.Gender;
import xyz.morphia.annotations.Embedded;

@Embedded
public class D4HQuery {
    private String ssn;
    private String country;
    private String city;
    private String province;
    @Embedded
    private Gender gender;
    @Embedded
    private Comparison ageComparison;
    private Integer age;
    @Embedded
    private BloodType bloodType;

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }
}
