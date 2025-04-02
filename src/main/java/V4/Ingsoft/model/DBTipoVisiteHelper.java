package V4.Ingsoft.model;

import java.util.ArrayList;

import V4.Ingsoft.controller.item.luoghi.StatusVisita;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.util.Date;

public class DBTipoVisiteHelper extends DBAbstractHelper<TipoVisita> {

    public DBTipoVisiteHelper() {
        super(TipoVisita.PATH, TipoVisita.class);

        getJson().forEach(tv -> cachedItems.put(tv.getUID(), tv));
    }

    public TipoVisita getTipiVisitaByUID(String uid) {
        return cachedItems.get(uid);
    }

    public ArrayList<TipoVisita> getTipiVisita() {
        return super.getItems();
    }

    public boolean addTipoVisita(TipoVisita toAdd) {
        if (cachedItems.get(toAdd.getUID()) != null)
            return false;
            cachedItems.put(toAdd.getUID(), toAdd);
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

            cachedItems.remove(toFind.getUID());
        return saveJson(getTipiVisita());
    }

    public boolean removeTipoVisitaByUID(String toRemoveUID) {
        if (cachedItems.get(toRemoveUID) == null)
            return false;

            cachedItems.remove(toRemoveUID);
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
            if (tv.getStatus() == StatusVisita.PENDING)
                tv.isMonthExpired(d);
        }
    }

    public ArrayList<TipoVisita> getTipoVisiteIstanziabili() {
        ArrayList<TipoVisita> out = new ArrayList<>();

        for (TipoVisita tv : getTipiVisita()) {
            if (tv.getStatus() != StatusVisita.PENDING)
                out.add(tv);
        }
        return out;
    }

    public boolean isNew() {
        return cachedItems.size() == 0;
    }

    public void close() {
        saveJson(getTipiVisita());
    }
}
