package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.user.D4HUser;
import org.bson.types.ObjectId;
import xyz.morphia.Key;

import java.util.Collection;

public class UserResource extends Resource<D4HUser> {

    private static UserResource _instance;

    private UserResource(){
        this.init();
    }

    public static UserResource getInstance(){
        if(_instance == null){
            _instance = new UserResource();
        }
        return _instance;
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
    public Collection<D4HUser> getAll() {
        return null;
    }

    @Override
    public D4HUser getById(String id) {
        return null;
    }

    @Override
    public D4HUser getById(ObjectId id) {
        return null;
    }

    @Override
    public void add(D4HUser o) {
        this.getDatastore().save(o);
    }

    @Override
    public void update(D4HUser o) {

    }

    @Override
    public void removeById(String id) {

    }

    @Override
    public void removeById(ObjectId id) {

    }

    @Override
    public void remove(D4HUser o) {

    }
}
