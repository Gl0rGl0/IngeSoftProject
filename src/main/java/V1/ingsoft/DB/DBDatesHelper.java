package V1.ingsoft.DB;

import V1.ingsoft.util.Date;

import java.util.ArrayList;

public class DBDatesHelper {
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

    public boolean removePrecludedDate(Date date) {
        return precludedDates.remove(date);
    }

    public void refreshPrecludedDate(Date date) {
        int monthCorrente = date.getMonth();

        for (Date d : precludedDates) {
            if (d.getMonth() == monthCorrente)
                precludedDates.remove(d);
        }
    }
}
