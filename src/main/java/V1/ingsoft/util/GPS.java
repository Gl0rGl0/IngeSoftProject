package V1.ingsoft.util;

public class GPS {
    double longitudine;
    double latitudine;

    public GPS(double longitudine, double latitudine) {
        this.longitudine = longitudine;
        this.latitudine = latitudine;
    }

    public GPS(String g) {
        String[] use = StringUtils.rimuoviParentesi(g).split(":");
        try {
            float lon = Float.parseFloat(use[0]);
            float lat = Float.parseFloat(use[1]);
            // Arrotonda a 3 decimali
            this.longitudine = Math.round(lon * 1000) / 1000f;
            this.latitudine = Math.round(lat * 1000) / 1000f;
        } catch (NumberFormatException e) {
            this.latitudine = 0;
            this.longitudine = 0;
        }
    }

    @Override
    public String toString() {
        return "(" + longitudine + ":" + latitudine + ")";
    }

    public double getLatitudine() {
        return this.latitudine;
    }

    public double getLongitudine() {
        return this.longitudine;
    }
}
