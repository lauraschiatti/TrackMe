package avila.schiatti.virdi.model.user;

import org.bson.types.ObjectId;
import xyz.morphia.annotations.*;

@Entity("user")
public abstract class D4HUser{
    @Id
    private ObjectId id;

    @Indexed(options = @IndexOptions(unique = true))
    private String email;
    private String password;

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

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public abstract D4HUserRole getRole();
}
