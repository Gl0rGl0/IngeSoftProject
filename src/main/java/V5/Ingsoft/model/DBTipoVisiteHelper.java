package V5.Ingsoft.model;

import java.util.ArrayList;

import V5.Ingsoft.controller.item.interfaces.DBWithStatus;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.controller.item.statuses.StatusItem;
import V5.Ingsoft.util.Date;

public class DBTipoVisiteHelper extends DBMapHelper<TipoVisita> implements DBWithStatus {

    public DBTipoVisiteHelper() {
        super(TipoVisita.PATH, TipoVisita.class);
    }

    public DBTipoVisiteHelper(ArrayList<TipoVisita> list) {
        super(TipoVisita.PATH, TipoVisita.class, list);
    }

    /**
     * Cerca e restituisce un Luogo in cache in base al name.
     *
     * @param name il name del luogo da cercare
     * @return il Luogo trovato, oppure null se non esiste.
     */
    public TipoVisita findTipoVisita(String name) {
        for (TipoVisita tv : getItems()) {
            if (tv.getTitle().equalsIgnoreCase(name)) {
                return tv;
            }
        }
        return null;
    }

    @Override
    public void checkItems(Date d) {
        for (TipoVisita tv : getItems()) {
            switch (tv.getStatus()) {
                case StatusItem.PENDING_ADD -> tv.checkStatus(d);
                case StatusItem.PENDING_REMOVE -> {
                    Date deletionDate = tv.getDeletionDate();
                    if (deletionDate != null && (deletionDate.equals(d) || deletionDate.isBefore(d)))
                        removeItem(tv.getUID());
                }
                case StatusItem.DISABLED -> removeItem(tv.getUID());
                default -> {
                }
            }
        }
    }

    public boolean isNew() {
        return cachedItems.isEmpty();
    }
}
