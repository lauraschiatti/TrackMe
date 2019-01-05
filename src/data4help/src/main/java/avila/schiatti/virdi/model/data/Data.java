package avila.schiatti.virdi.model.data;

import avila.schiatti.virdi.model.user.Individual;
import org.bson.types.ObjectId;
import xyz.morphia.annotations.Embedded;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.Reference;

import java.time.LocalDateTime;

@Entity("data")
public class Data {
    @Id
    private ObjectId id;

    @Embedded
    private Location location;
    private LocalDateTime timestamp = LocalDateTime.now();
    @Embedded
    private HealthStatus healthStatus;

    @Reference(idOnly = true, lazy = true)
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
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
