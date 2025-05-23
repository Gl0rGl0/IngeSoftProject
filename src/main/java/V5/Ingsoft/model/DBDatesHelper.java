package V5.Ingsoft.model;

import V5.Ingsoft.controller.item.interfaces.Product;
import V5.Ingsoft.util.Date;

import java.time.Month;
import java.util.ArrayList;

public class DBDatesHelper implements Product{
    public static final String CLASSNAME = DBDatesHelper.class.getSimpleName(); 
    private final ArrayList<Date> precludedDates = new ArrayList<>();

    /**
     * Restituisce il set delle date speciali.
     *
     * @return HashSet<Date> delle date speciali
     */
    public ArrayList<Date> getPrecludedDates() {
        return precludedDates;
    }

    // /**
    // * Aggiunge la data speciale (con commento) nel file in modalità append.
    // * Restituisce true se l'operazione va a buon fine, false altrimenti.
    // *
    // * @param date l'oggetto Date da salvare
    // * @return true se la scrittura è andata a buon fine, false in caso di errore
    // */
    public boolean addPrecludedDate(Date date) {
        if (precludedDates.contains(date)) {
            return false;
        } else {
            return precludedDates.add(date);
        }
    }

    public void removePrecludedDate(Date date) {
        precludedDates.remove(date);
    }

    public void refreshPrecludedDate(Date date) {
        Month monthPassato = date.getMonth().minus(1);

        precludedDates.removeIf(d -> d.getMonth() == monthPassato);
    }
}
