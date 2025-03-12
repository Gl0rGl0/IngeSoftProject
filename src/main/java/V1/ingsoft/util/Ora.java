package V1.ingsoft.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class Ora {
    int hh;
    int mm;

    @JsonCreator
    public Ora(@JsonProperty("hh") int hh, @JsonProperty("mm") int mm) {
        this.hh = hh;
        this.mm = mm;
    }

    public Ora(String o) {
        String[] sub = o.split(":");
        this.hh = Integer.parseInt(sub[0]);
        this.mm = Integer.parseInt(sub[1]);
    }

    @Override
    public String toString() {
        return String.format("%d:%d", hh, mm);
    }

    public boolean isequal(Ora other) {
        return other.hh == this.hh && other.mm == this.mm;
    }

    public int getMinuti() {
        return this.hh * 60 + this.mm;
    }
}
