package V1.ingsoft.util;

public class Ora {
    int hh;
    int mm;

    public Ora(int hh, int mm) {
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
