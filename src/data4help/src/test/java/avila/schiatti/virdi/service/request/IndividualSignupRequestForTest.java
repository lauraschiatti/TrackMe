package avila.schiatti.virdi.service.request;

import avila.schiatti.virdi.model.data.Address;
import avila.schiatti.virdi.model.data.BloodType;
import avila.schiatti.virdi.model.data.Gender;

import java.time.LocalDate;

public class IndividualSignupRequestForTest {
    private String email;
    private String password;
    private String name;
    private String ssn;
    private String birthDate;
    private Gender gender;
    private Address address;
    private BloodType bloodType;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate.toString();
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
}
