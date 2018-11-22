package avila.schiatti.virdi.model.data;

import xyz.morphia.annotations.Embedded;

@Embedded
public class Location {
    private Long latitude;
    private Long longitude;

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }
}
