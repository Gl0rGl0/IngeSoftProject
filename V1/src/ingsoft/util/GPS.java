package ingsoft.util;

public class GPS {
    double longitudine;
    double latitudine;

    public GPS(double longitudine, double latitudine){
        this.longitudine = longitudine;
        this.latitudine = latitudine;
    }

    public String toString(){
        return "(" + longitudine + "," + latitudine + ")";
    }
}
