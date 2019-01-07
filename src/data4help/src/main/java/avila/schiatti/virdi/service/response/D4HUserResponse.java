package avila.schiatti.virdi.service.response;

import avila.schiatti.virdi.model.data.Address;
import avila.schiatti.virdi.model.data.BloodType;
import avila.schiatti.virdi.model.data.Gender;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.TPConfiguration;
import avila.schiatti.virdi.model.user.ThirdParty;

import java.time.LocalDate;

public class D4HUserResponse {
    private String name;
    private String ssn;
    private Float weight;
    private Float height;
    private LocalDate birthDate;
    private Gender gender;
    private Address address;
    private BloodType bloodType;

    private String phone;
    private String taxCode;
    private String secretKey;
    private String appId;
    private TPConfiguration config;

    private Long pendingRequests;
    private Long approvedRequests;
    private Long rejectedRequests;

    private D4HUserResponse(){}

    public static D4HUserResponse fromIndividual(Individual i){
        D4HUserResponse res = new D4HUserResponse();
        res.name = i.getName();
        res.ssn = i.getSsn();
        res.weight = i.getWeight();
        res.height = i.getHeight();
        res.birthDate = i.getBirthDate();
        res.gender = i.getGender();
        res.address = i.getAddress();
        res.bloodType = i.getBloodType();
        return res;
    }

    public static D4HUserResponse fromThirdParty(ThirdParty tp){
        D4HUserResponse res = new D4HUserResponse();
        res.name = tp.getName();
        res.phone = tp.getPhone();
        res.taxCode = tp.getTaxCode();
        res.secretKey = tp.getSecretKey();
        res.appId = tp.getAppId();
        res.config = tp.getConfig();
        return res;
    }

    public Long getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(Long pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public Long getApprovedRequests() {
        return approvedRequests;
    }

    public void setApprovedRequests(Long approvedRequests) {
        this.approvedRequests = approvedRequests;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public TPConfiguration getConfig() {
        return config;
    }

    public void setConfig(TPConfiguration config) {
        this.config = config;
    }

    public Long getRejectedRequests() {
        return rejectedRequests;
    }

    public void setRejectedRequests(Long rejectedRequests) {
        this.rejectedRequests = rejectedRequests;
    }
}
