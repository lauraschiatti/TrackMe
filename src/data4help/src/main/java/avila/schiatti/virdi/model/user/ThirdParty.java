package avila.schiatti.virdi.model.user;

import xyz.morphia.annotations.*;

@Entity("user")
public class ThirdParty extends D4HUser {
    private String certificate;
    private String name;
    private String phone;
    private String code;
    @Indexed(options = @IndexOptions(partialFilter = "{ taxCode: { $exists : true } }", unique = true))
    private String taxCode;
    private String secretKey;
    private String appId;
    @Embedded
    private TPConfiguration config;

    @Override
    public D4HUserRole getRole() {
        return D4HUserRole.THIRD_PARTY;
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

    public TPConfiguration getConfig() {
        if(config == null){
            config = new TPConfiguration();
        }

        return config;
    }

    public void setConfig(TPConfiguration config) {
        this.config = config;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
}
