package avila.schiatti.virdi.configuration;

import java.util.HashMap;

public class TrackMeConfiguration {
    private HashMap<String, String> mongodb;
    private HashMap<String, String> redis;

    public HashMap<String, String> getMongodb() {
        return mongodb;
    }

    public String getMongoDBName() {
        return this.mongodb.get("db_name");
    }

    public String getMongoURI() {
        return this.mongodb.get("uri");
    }

    public void setMongodb(HashMap<String, String> mongodb) {
        this.mongodb = mongodb;
    }

    public HashMap<String, String> getRedis() {
        return redis;
    }

    public String getRedisDBNumber() {
        return this.redis.get("db_number");
    }

    public String getRedisURI() {
        return this.redis.get("uri");
    }

    public void setRedis(HashMap<String, String> redis) {
        this.redis = redis;
    }
}
