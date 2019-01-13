package avila.schiatti.virdi.model.health;

public class Threshold {
    private Integer minAge;
    private Integer maxAge;
    private Double minValue;
    private Double maxValue;
    private HealthParameter parameter;

    public Threshold(Integer minAge, Integer maxAge, Double minValue, Double maxValue, HealthParameter parameter) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.parameter = parameter;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public HealthParameter getParameter() {
        return parameter;
    }

    public void setParameter(HealthParameter parameter) {
        this.parameter = parameter;
    }
}
