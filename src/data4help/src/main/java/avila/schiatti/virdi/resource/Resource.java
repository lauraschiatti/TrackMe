package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.database.DBManager;
import org.bson.types.ObjectId;
import xyz.morphia.Datastore;

import java.util.Collection;

public abstract class Resource<T>  {

    private DBManager dbManager;
    protected Datastore datastore;

    /**
     * Only for test constructor
     * @param dbManager
     * @param datastore
     */
    public Resource(DBManager dbManager, Datastore datastore){
        this.dbManager = dbManager;
        this.datastore = datastore;
    }

    public Resource() {
        this.dbManager = DBManager.getInstance();
        this.datastore = dbManager.getDatastore();
    }

    public Datastore getDatastore(){
        return datastore;
    }

    Collection<T> getAll(){
        throw new UnsupportedOperationException();
    }

    T getById(String id){
        throw new UnsupportedOperationException();
    }

    T getById(ObjectId id){
        throw new UnsupportedOperationException();
    }

    void add(T o){
        throw new UnsupportedOperationException();
    }

    void update(T o){
        throw new UnsupportedOperationException();
    }

    void removeById(String id){
        throw new UnsupportedOperationException();
    }

    void removeById(ObjectId id){
        throw new UnsupportedOperationException();
    }

    void remove(T o){
        throw new UnsupportedOperationException();
    }
}
