package avila.schiatti.virdi.database;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;
import xyz.morphia.mapping.Mapper;
import xyz.morphia.mapping.MapperOptions;

public class DBManager {
    private final static String MODELS_PACKAGE = "avila.schiatti.virdi.model";
    private final static StaticConfiguration config = StaticConfiguration.getInstance();

    private final static DBManager _instance = new DBManager();

    private Datastore datastore;

    private DBManager(){
        MapperOptions mapperOptions = new MapperOptions();
        mapperOptions.setMapSubPackages(true);

        Mapper mapper = new Mapper(mapperOptions);
        Morphia morphia = (new Morphia(mapper)).mapPackage(MODELS_PACKAGE);;

        MongoClientURI mongoClientURI = new MongoClientURI(config.getMongoDBConnectionString());
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        datastore = morphia.createDatastore(mongoClient, config.getMongoDBDatabase());
        datastore.ensureIndexes();
    }

    public static DBManager getInstance(){
        return _instance;
    }

    public final Datastore getDatastore(){
        return _instance.datastore;
    }

}
