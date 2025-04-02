package V4.Ingsoft.model;

import java.util.ArrayList;

import V4.Ingsoft.controller.item.luoghi.Luogo;
import V4.Ingsoft.util.GPS;

public class DBLuoghiHelper extends DBAbstractHelper<Luogo> {

    private ArrayList<String> tipoVisitaUIDs;

    public DBLuoghiHelper() {
        super(Luogo.PATH, Luogo.class);

        getJson().forEach(l -> cachedItems.put(l.getUID(), l));
    }

    public void addTipoVisita(String tipoVisitaUID) {
        if (!tipoVisitaUIDs.contains(tipoVisitaUID))
            tipoVisitaUIDs.add(tipoVisitaUID);
    }

    public ArrayList<String> getTipoVisitaUIDs() {
        return this.tipoVisitaUIDs;
    }

    public Luogo getLuogoByUID(String uid) {
        return cachedItems.get(uid);
    }

    public ArrayList<Luogo> getLuoghi() {
        return super.getItems();
    }

    public boolean addLuogo(String name, String description, GPS gps) {
        return addLuogo(new Luogo(name, description, gps));
    }

    /**
     * Aggiunge un nuovo Luogo nel file delle proprietà.
     * Simile a addPersona(), ma adattato per Luogo.
     *
     * @param luogo il luogo da aggiungere
     * @return true se l'aggiunta è andata a buon fine, false altrimenti.
     */
    public boolean addLuogo(Luogo toAdd) {
        if (cachedItems.get(toAdd.getUID()) != null)
            return false;

        cachedItems.put(toAdd.getUID(), toAdd);
        return saveJson(getLuoghi());
    }

    /**
     * Rimuove un Luogo dal file delle proprietà, basandosi sul name.
     * Simile a removePersona(), ma adattato per Luogo.
     *
     * @param name il name del luogo da rimuovere
     * @return true se il luogo è stato rimosso, false in caso di errori.
     */
    public boolean removeLuogo(String toRemove) {
        Luogo toFind = findLuogo(toRemove);

        if (toFind == null)
            return false;

        cachedItems.remove(toFind.getUID());
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
        return cachedItems.size() == 0;
    }

    public boolean containsLuogoUID(String uidLuogo) {
        if (uidLuogo == null || uidLuogo.strip().length() == 0) // PRECONDIZIONE
            return false;
        return cachedItems.containsKey(uidLuogo);
    }

    public void close() {
        saveJson(getLuoghi());
    }
}