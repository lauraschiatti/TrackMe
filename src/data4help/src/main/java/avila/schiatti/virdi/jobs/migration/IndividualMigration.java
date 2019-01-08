package avila.schiatti.virdi.jobs.migration;

import avila.schiatti.virdi.model.data.Address;
import avila.schiatti.virdi.model.data.BloodType;
import avila.schiatti.virdi.model.data.Gender;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.service.authentication.AuthenticationManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IndividualMigration {
    //                                                                      "07/03/1955"
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

    private String name;
    private String ssn;
    private String birthDate;
    private Gender gender;
    private String city;
    private String province;
    private String country;
    private Float height;
    private Float weight;
    private BloodType bloodType;
    private String  email;
    private String password;

    public Individual buildIndividual(){
        Individual i = new Individual();
        i.setHeight(height);
        i.setWeight(weight);
        i.setName(name);
        i.setSsn(ssn);
        i.setBirthDate(LocalDate.parse(birthDate, formatter));
        i.setAddress(new Address());
        i.getAddress().setCountry(country);
        i.getAddress().setProvince(province);
        i.getAddress().setCity(city);
        i.setGender(gender);
        i.setBloodType(bloodType);
        i.setPassword(AuthenticationManager.hashPassword(password));
        i.setEmail(email);
        return i;
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

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

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
        this.password = AuthenticationManager.hashPassword(password);
    }
}