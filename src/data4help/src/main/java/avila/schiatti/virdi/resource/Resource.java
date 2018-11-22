package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.database.DBManager;
import org.bson.types.ObjectId;
import xyz.morphia.Datastore;

import java.util.Collection;

abstract class Resource<T>  {

    private DBManager dbManager;

    final void init(){
        this.dbManager = DBManager.getInstance();
    }

    protected final DBManager getDbManager() {
        return dbManager;
    }

    final Datastore getDatastore() {
        return dbManager.getDatastore();
    }

    abstract Collection<T> getAll();
    abstract T getById(String id);
    abstract T getById(ObjectId id);
    abstract void add(T o);
    abstract void update(T o);
    abstract void removeById(String id);
    abstract void removeById(ObjectId id);
    abstract void remove(T o);
}
