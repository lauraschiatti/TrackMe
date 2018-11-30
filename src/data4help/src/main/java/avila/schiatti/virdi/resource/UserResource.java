package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.model.user.D4HUser;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.TestOnly;
import xyz.morphia.Key;

import java.util.Collection;

public class UserResource extends Resource<D4HUser> {

    @TestOnly
    public UserResource(DBManager dbManager){
        super(dbManager);
    }

    private UserResource(){
        super();
    }

    public static UserResource create(){
        return new UserResource();
    }

    public D4HUser getByEmailAndPass(String email, String password){
        return this.getDatastore()
                .find(D4HUser.class)
                .field("email")
                .equal(email)
                .field("password")
                .equal(password)
                .get();
    }

    @Override
    public void add(D4HUser o) {
        this.getDatastore().save(o);
    }
}
