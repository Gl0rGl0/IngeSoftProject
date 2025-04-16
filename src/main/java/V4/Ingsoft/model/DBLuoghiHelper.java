package V4.Ingsoft.model;

import V4.Ingsoft.controller.item.luoghi.Luogo;
import V4.Ingsoft.util.Date;

import java.util.ArrayList;

public class DBLuoghiHelper extends DBAbstractHelper<Luogo> {

    public DBLuoghiHelper() {
        super(Luogo.PATH, Luogo.class);

        getJson().forEach(l -> cachedItems.put(l.getUID(), l));
    }

    public Luogo getLuogoByUID(String uid) {
        return cachedItems.get(uid);
    }

    public ArrayList<Luogo> getLuoghi() {
        return super.getItems();
    }

    /**
     * Aggiunge un nuovo Luogo nel file delle proprietà.
     * Simile a addPersona(), ma adattato per Luogo.
     *
     * @param toAdd il luogo da aggiungere
     * @return true se l'aggiunta è andata a buon fine, false altrimenti.
     */
    public boolean addLuogo(Luogo toAdd) {
        if (cachedItems.get(toAdd.getUID()) != null)
            return false;

        cachedItems.put(toAdd.getUID(), toAdd);
        return saveJson(getLuoghi());
    }

    public boolean removeLuogoByUID(String toRemoveUID) {
        if (cachedItems.get(toRemoveUID) == null)
            return false;

        cachedItems.remove(toRemoveUID);
        return saveJson(getLuoghi());
    }

    /**
     * Cerca e restituisce un Luogo in cache in base al name.
     *
     * @param name il name del luogo da cercare
     * @return il Luogo trovato, oppure null se non esiste.
     */
    public Luogo findLuogo(String name) {
        for (Luogo l : getLuoghi()) {
            if (l.getName().equalsIgnoreCase(name)) {
                return l;
            }
        }
        return null;
    }

    public boolean isNew() {
        return cachedItems.isEmpty();
    }

    public void close() {
        saveJson(getLuoghi());
    }

    public void checkLuoghi(Date d) {
        for (Luogo l : getLuoghi()) {
            l.checkStatus(d);
        }
    }
}