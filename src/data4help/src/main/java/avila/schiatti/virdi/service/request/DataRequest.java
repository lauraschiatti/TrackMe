package avila.schiatti.virdi.service.request;

import avila.schiatti.virdi.model.data.HealthStatus;
import avila.schiatti.virdi.model.data.Location;

public class DataRequest {
    private String ssn;
    private HealthStatus healthStatus;
    private Location location;

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
