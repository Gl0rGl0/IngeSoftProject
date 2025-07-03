package V5.Ingsoft.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class Time {
    int hh;
    int mm;

    @JsonCreator
    public Time(@JsonProperty("hh") int hh, @JsonProperty("mm") int mm) {
        this.hh = hh;
        this.mm = mm;
    }

    public Time(String o) {
        String[] sub = o.split(":");
        this.hh = Integer.parseInt(sub[0]);
        this.mm = Integer.parseInt(sub[1]);
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hh, mm);
    }


    public int getMinutes() {
        return this.hh * 60 + this.mm;
    }

}
