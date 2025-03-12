package V1.ingsoft.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class GPS {
    double longitude;
    double latitude;

    @JsonCreator
    public GPS(
            @JsonProperty("longitude") double longitude,
            @JsonProperty("latitude") double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public GPS(String g) {
        String[] use = StringUtils.removeParentheses(g).split(":");
        try {
            float lon = Float.parseFloat(use[0]);
            float lat = Float.parseFloat(use[1]);
            // Arrotonda a 3 decimali
            this.longitude = Math.round(lon * 1000) / 1000f;
            this.latitude = Math.round(lat * 1000) / 1000f;
        } catch (NumberFormatException e) {
            this.latitude = 0;
            this.longitude = 0;
        }
    }

    @Override
    public String toString() {
        return "(" + longitude + ":" + latitude + ")";
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }
}
