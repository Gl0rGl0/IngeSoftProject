package V5.Ingsoft.model;

import V5.Ingsoft.controller.item.interfaces.DBWithStatus;
import V5.Ingsoft.controller.item.luoghi.Luogo;

public class DBLuoghiHelper extends DBMapHelper<Luogo> implements DBWithStatus {

    public DBLuoghiHelper() {
        super(Luogo.PATH, Luogo.class);
    }

    /**
     * Cerca e restituisce un Luogo in cache in base al name.
     *
     * @param name il name del luogo da cercare
     * @return il Luogo trovato, oppure null se non esiste.
     */
    public Luogo findLuogo(String name) {
        for (Luogo l : getItems()) {
            if (l.getName().equalsIgnoreCase(name)) {
                return l;
            }
        }
        return null;
    }

    public boolean isNew() {
        return cachedItems.isEmpty();
    }
}