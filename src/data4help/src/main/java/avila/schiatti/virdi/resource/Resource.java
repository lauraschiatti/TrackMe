package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.database.DBManager;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.TestOnly;
import xyz.morphia.Datastore;

import java.util.Collection;

abstract class Resource<T>  {

    private DBManager dbManager;

    /**
     * Only for test constructor
     * @param dbManager
     */
    Resource(DBManager dbManager){
        this.dbManager = dbManager;
    }

    Resource() {
        this.dbManager = DBManager.getInstance();
    }

    public Datastore getDatastore() {
        return dbManager.getDatastore();
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
