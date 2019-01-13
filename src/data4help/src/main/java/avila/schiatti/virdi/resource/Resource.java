package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.database.DBManager;
import avila.schiatti.virdi.exception.TrackMeException;
import com.mongodb.DuplicateKeyException;
import org.bson.types.ObjectId;
import xyz.morphia.Datastore;
import xyz.morphia.query.Query;

import java.util.Collection;

public abstract class Resource<T>  {

    private Class<T> clazz;
    protected Datastore datastore;

    /**
     * Only for test constructor
     * @param datastore
     * @param aClass
     */
    public Resource(Datastore datastore, Class<T> aClass){
        this.datastore = datastore;
        this.clazz = aClass;
    }

    public Resource(Class<T> aClass) {
        this.datastore = DBManager.getInstance()
                .getDatastore();
        this.clazz = aClass;
    }

    public Collection<T> getAll(){
        return datastore.find(clazz).asList();
    }

    public T getById(String id){
        return this.getById(new ObjectId(id));
    }

    public T getById(ObjectId id){
        return datastore.find(clazz)
                .field("id")
                .equal(id)
                .get();
    }

    public void add(T o){
        try {
            datastore.save(o);
        } catch (DuplicateKeyException dkex){
            throw TrackMeException.transformFromMongoException(dkex);
        }
    }

    public void update(T o){
        datastore.save(o);
    }

    public void removeById(String id){
        removeById(new ObjectId(id));
    }

    public void removeById(ObjectId id){
        Query<T> query = datastore.createQuery(clazz)
                .field("id")
                .equal(id);

        this.datastore.delete(query);
    }

    public void remove(T o){
        this.datastore.delete(o);
    }

    public Long countAll(){
        return datastore.createQuery(clazz).count();
    }
}
