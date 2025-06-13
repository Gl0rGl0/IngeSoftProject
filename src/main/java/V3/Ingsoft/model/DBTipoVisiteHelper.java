package V3.Ingsoft.model;

import V3.Ingsoft.controller.item.StatusItem;
import V3.Ingsoft.controller.item.luoghi.TipoVisita;
import V3.Ingsoft.util.Date;

import java.util.ArrayList;

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

    public void removeTipoVisitaByUID(String toRemoveUID) {
        if (cachedItems.get(toRemoveUID) == null)
            return;

        cachedItems.remove(toRemoveUID);
        saveJson(getTipiVisita());
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

    public void checkTipoVisite(Date d) {
        for (TipoVisita tv : getTipiVisita()) {
            switch (tv.getStatus()) {
                case PENDING_ADD -> tv.checkStatus(d);
                case PENDING_REMOVE -> {
                    Date deletionDate = tv.getDeletionDate();
                    if (deletionDate != null && (deletionDate.equals(d) || deletionDate.isBefore(d)))
                        removeTipoVisitaByUID(tv.getUID());
                }
                case DISABLED -> removeTipoVisitaByUID(tv.getUID());
                case ACTIVE -> {
                }
            }
        }
    }

    public ArrayList<TipoVisita> getTipoVisiteIstanziabili() {
        ArrayList<TipoVisita> out = new ArrayList<>();

        for (TipoVisita tv : getTipiVisita()) {
            if (tv.getStatus() == StatusItem.ACTIVE || tv.getStatus() == StatusItem.PENDING_REMOVE) //or != PENDING_ADD and != DISABLED
                out.add(tv);
        }
        return out;
    }

    public boolean isNew() {
        return cachedItems.isEmpty();
    }

    public void close() {
        saveJson(getTipiVisita());
    }
}
