package V1.ingsoft.DB;

import V1.ingsoft.luoghi.Luogo;
import V1.ingsoft.util.GPS;
import java.util.ArrayList;
import java.util.HashMap;

public class DBLuoghiHelper extends DBAbstractHelper<Luogo> {

    private final HashMap<String, Luogo> cachedLuoghi = new HashMap<>();

    private ArrayList<String> tipoVisitaUIDs;

    public DBLuoghiHelper() {
        super(Luogo.PATH, Luogo.class);

        getJson().forEach(l -> cachedLuoghi.put(l.getUID(), l));
    }

    public void addTipoVisita(String tipoVisitaUID) {
        if (!tipoVisitaUIDs.contains(tipoVisitaUID))
            tipoVisitaUIDs.add(tipoVisitaUID);
    }

    public ArrayList<String> getTipoVisitaUIDs() {
        return this.tipoVisitaUIDs;
    }

    public Luogo getLuogoByUID(String uid) {
        return cachedLuoghi.get(uid);
    }

    public ArrayList<Luogo> getLuoghi() {
        return new ArrayList<>(cachedLuoghi.values());
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
        if (cachedLuoghi.get(toAdd.getUID()) != null)
            return false;

        cachedLuoghi.put(toAdd.getUID(), toAdd);
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

        cachedLuoghi.remove(toFind.getUID());
        return saveJson(getLuoghi());
    }

    public boolean removeLuogoByUID(String toRemoveUID) {
        if (cachedLuoghi.get(toRemoveUID) == null)
            return false;

        cachedLuoghi.remove(toRemoveUID);
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
        return cachedLuoghi.size() == 0;
    }

    public boolean containsLuogoUID(String uidLuogo) {
        if (uidLuogo == null) // PRECONDIZIONE
            return false;
        return cachedLuoghi.containsKey(uidLuogo);
    }

    public void close() {
        saveJson(getLuoghi());
    }
}