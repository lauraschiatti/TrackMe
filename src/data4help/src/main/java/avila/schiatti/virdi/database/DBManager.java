package avila.schiatti.virdi.database;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;

public class DBManager {
    private final static String MODELS_PACKAGE = "avila.schiatti.virdi.model";
    private final static Morphia morphia = new Morphia();
    private final static StaticConfiguration config = StaticConfiguration.getInstance();

    private static DBManager _instance;

    private Datastore datastore;

    private DBManager(){
        morphia.mapPackage(MODELS_PACKAGE);
        MongoClientURI mongoClientURI = new MongoClientURI(config.getMongoDBConnectionString());
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        datastore = morphia.createDatastore(mongoClient, config.getMongoDBDatabase());
    }

    public static DBManager getInstance(){
        if(_instance == null){
            _instance = new DBManager();
        }
        return _instance;
    }

    public final Datastore getDatastore(){
        return _instance.datastore;
    }

}
