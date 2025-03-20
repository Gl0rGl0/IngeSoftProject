package V1.ingsoft.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

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
        return String.format("%d:%d", hh, mm);
    }

    public boolean isEqual(Time other) {
        return other.hh == this.hh && other.mm == this.mm;
    }

    public int getMinutes() {
        return this.hh * 60 + this.mm;
    }

    public boolean greaterThen(Time other) {
        if (this.hh == other.hh) {
            return this.mm > other.mm;
        }

        return this.hh > other.hh;
    }
}
