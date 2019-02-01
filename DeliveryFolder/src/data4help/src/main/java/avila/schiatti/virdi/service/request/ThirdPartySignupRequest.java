package avila.schiatti.virdi.service.request;

import avila.schiatti.virdi.model.user.TPConfiguration;

public class ThirdPartySignupRequest {
    private String email;
    private String password;
    private String certificate;
    private String name;
    private String phone;
    private String taxCode;
    private TPConfiguration config;

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

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public TPConfiguration getConfig() {
        return config;
    }

    public void setConfig(TPConfiguration config) {
        this.config = config;
    }
}
