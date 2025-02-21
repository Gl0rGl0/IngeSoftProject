package ingsoft.util;

public class GPS {
    double longitudine;
    double latitudine;

    public GPS(double longitudine, double latitudine){
        this.longitudine = longitudine;
        this.latitudine = latitudine;
    }

    public GPS(String g){
        String[] use = StringUtils.rimuoviParentesi(g).split(":");
        try {
            this.longitudine = Integer.parseInt(use[0]);
            this.latitudine = Integer.parseInt(use[1]);
        } catch (NumberFormatException e) {
            this.latitudine = 0;
            this.longitudine = 0;
        }
    }

    @Override
    public String toString(){
        return "(" + longitudine + ":" + latitudine + ")";
    }

    public double getLatitudine(){
        return this.latitudine;
    }

    public double getLongitudine(){
        return this.longitudine;
    }
}
