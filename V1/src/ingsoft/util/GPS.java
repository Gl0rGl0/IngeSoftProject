package ingsoft.util;

public class GPS {
    double longitudine;
    double latitudine;

    public GPS(double longitudine, double latitudine){
        this.longitudine = longitudine;
        this.latitudine = latitudine;
    }

    @Override
    public String toString(){
        return "(" + longitudine + "," + latitudine + ")";
    }
}
