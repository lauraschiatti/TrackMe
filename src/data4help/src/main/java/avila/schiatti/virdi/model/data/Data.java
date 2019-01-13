package avila.schiatti.virdi.model.data;

import avila.schiatti.virdi.model.user.Individual;
import org.bson.types.ObjectId;
import xyz.morphia.annotations.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity("data")
public class Data {
    @Id
    private ObjectId id;

    @Embedded
    private Location location;
    private long timestamp = Timestamp.valueOf(LocalDateTime.now()).getTime();
    @Embedded
    private HealthStatus healthStatus;

    @Indexed
    @Reference(idOnly = true)
    private Individual individual;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Location getLocation() {
        if(location == null){
            location = new Location();
        }
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public HealthStatus getHealthStatus() {
        if(healthStatus == null){
            healthStatus = new HealthStatus();
        }
        return healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }
}
