package avila.schiatti.virdi.model.data;

import xyz.morphia.annotations.Embedded;

@Embedded
public class HealthStatus {
    private Integer heartRate;
    private Integer bloodPreasure;
    private Integer bodyTemperature;
    private Integer bloodOxygen;

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getBloodPreasure() {
        return bloodPreasure;
    }

    public void setBloodPreasure(Integer bloodPreasure) {
        this.bloodPreasure = bloodPreasure;
    }

    public Integer getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(Integer bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }

    public Integer getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(Integer bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }
}
