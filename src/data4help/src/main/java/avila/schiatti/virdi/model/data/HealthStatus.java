package avila.schiatti.virdi.model.data;

import xyz.morphia.annotations.Embedded;

@Embedded
public class HealthStatus {
    private Float heartRate;
    private Float bloodPressure;
    private Float bodyTemperature;
    private Float bloodOxygen;

    public Float getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Float heartRate) {
        this.heartRate = heartRate;
    }

    public Float getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(Float bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Float getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(Float bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }

    public Float getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(Float bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }
}
