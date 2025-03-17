package V1.ingsoft.DB;

import V1.ingsoft.luoghi.StatusVisita;
import V1.ingsoft.luoghi.TipoVisita;
import V1.ingsoft.util.Date;
import java.util.ArrayList;
import java.util.HashMap;

public class DBTipoVisiteHelper extends DBAbstractHelper<TipoVisita> {
    private final HashMap<String, TipoVisita> cachedTipiVisita = new HashMap<>();

    public DBTipoVisiteHelper() {
        super(TipoVisita.PATH, TipoVisita.class);

        getJson().forEach(tv -> cachedTipiVisita.put(tv.getUID(), tv));
    }

    public TipoVisita getTipiVisitaByUID(String uid) {
        return cachedTipiVisita.get(uid);
    }

    public ArrayList<TipoVisita> getTipiVisita() {
        return new ArrayList<>(cachedTipiVisita.values());
    }

    public boolean addTipoVisita(TipoVisita toAdd) {
        if (cachedTipiVisita.get(toAdd.getUID()) != null)
            return false;

        cachedTipiVisita.put(toAdd.getUID(), toAdd);
        return saveJson(getTipiVisita());
    }

    /**
     * Rimuove un Luogo dal file delle proprietà, basandosi sul name.
     * Simile a removePersona(), ma adattato per Luogo.
     *
     * @param name il name del luogo da rimuovere
     * @return true se il luogo è stato rimosso, false in caso di errori.
     */
    public boolean removeTipoVisita(String toRemove) {
        TipoVisita toFind = findTipoVisita(toRemove);

        if (toFind == null)
            return false;

        cachedTipiVisita.remove(toFind.getUID());
        return saveJson(getTipiVisita());
    }

    public boolean removeTipoVisitaByUID(String toRemoveUID) {
        if (cachedTipiVisita.get(toRemoveUID) == null)
            return false;

        cachedTipiVisita.remove(toRemoveUID);
        return saveJson(getTipiVisita());
    }

    /**
     * Cerca e restituisce un Luogo in cache in base al name.
     *
     * @param name il name del luogo da cercare
     * @return il Luogo trovato, oppure null se non esiste.
     */
    public TipoVisita findTipoVisita(String name) {
        for (TipoVisita tv : getTipiVisita()) {
            if (tv.getTitle().equalsIgnoreCase(name)) {
                return tv;
            }
        }
        return null;
    }

    public void checkTipoVisiteAttese(Date d) {
        for (TipoVisita tv : getTipiVisita()) {
            if (tv.getStatus() == StatusVisita.ATTESA)
                tv.isMonthExpired(d);
        }
    }

    public ArrayList<TipoVisita> getTipoVisiteIstanziabili() {
        ArrayList<TipoVisita> out = new ArrayList<>();

        for (TipoVisita tv : getTipiVisita()) {
            if (tv.getStatus() != StatusVisita.ATTESA)
                out.add(tv);
        }
        return out;
    }

    public boolean isNew() {
        return cachedTipiVisita.size() == 0;
    }
}
