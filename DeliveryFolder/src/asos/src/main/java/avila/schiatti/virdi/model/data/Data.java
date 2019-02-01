package avila.schiatti.virdi.model.data;

public class Data {
    private Location location;
    private HealthStatus healthStatus;

    public Location getLocation() {
       return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }
}
