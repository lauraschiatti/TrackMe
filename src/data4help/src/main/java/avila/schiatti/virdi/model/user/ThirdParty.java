package avila.schiatti.virdi.model.user;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;
import xyz.morphia.annotations.Embedded;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;

@Entity("user")
public class ThirdParty extends D4HUser {
    private String certificate;
    private String name;
    private String phone;
    @Embedded
    private TPConfiguration config;

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
        return config;
    }

    public void setConfig(TPConfiguration config) {
        this.config = config;
    }
}
