package V1.ingsoft.DB;

import V1.ingsoft.luoghi.StatusVisita;
import V1.ingsoft.luoghi.Visita;
import V1.ingsoft.util.Date;
import java.util.ArrayList;
import java.util.HashMap;

public class DBVisiteHelper extends DBAbstractHelper<Visita> {

    private final HashMap<String, Visita> cachedVisite = new HashMap<>();
    private ArrayList<Visita> archivio;

    public DBVisiteHelper() {
        super(Visita.PATH, Visita.class);

        getJson().forEach(v -> archivio.add(v));
    }

    /**
     * Legge il file delle proprietà e restituisce la lista delle visite.
     * Simile a getPersonList() in DBAbstractPersonaHelper, ma adattato per Visita.
     */
    public ArrayList<Visita> getVisite() {
        return new ArrayList<>(cachedVisite.values());
    }

    /**
     * Aggiunge una nuova Visita nel file delle proprietà.
     * Simile a addPersona(), ma adattato per Visita.
     *
     * @param visita la visita da aggiungere
     * @return true se l'aggiunta è andata a buon fine, false altrimenti.
     */
    public void addVisita(Visita toAdd) {
        cachedVisite.put(toAdd.getUID(), toAdd);
    }

    /**
     * Rimuove una Visita dal file delle proprietà, basandosi sul nome.
     * Simile a removePersona(), ma adattato per Visita.
     *
     * @param nome il nome della visita da rimuovere
     * @return true se la visita è stata rimossa, false in caso di errori.
     */
    public void removeVisita(String nome, String data) {
        Visita toRemove = findVisita(nome, data);
        if (toRemove == null)
            return;
        cachedVisite.remove(toRemove.getUID());
    }

    /**
     * Cerca e restituisce una Visita in cache in base al nome.
     *
     * @param nome il nome della visita da cercare
     * @return la Visita trovata, oppure null se non esiste.
     */
    public Visita findVisita(String titolo, String data) {
        for (Visita v : cachedVisite.values()) {
            if (v.getTitolo().equalsIgnoreCase(titolo) && v.getData().toString().equals(data)) {
                return v;
            }
        }
        return null;
    }

    public ArrayList<Visita> getCompletate() {
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getVisite()) {
            if (v.getStatus() == StatusVisita.COMPLETA)
                out.add(v);
        }

        return out;
    }

    public ArrayList<Visita> getConfermate() {
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getVisite()) {
            if (v.getStatus() == StatusVisita.CONFERMATA)
                out.add(v);
        }

        return out;
    }

    public ArrayList<Visita> getCancellate() {
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getVisite()) {
            if (v.getStatus() == StatusVisita.CANCELLATA)
                out.add(v);
        }

        return out;
    }

    public ArrayList<Visita> getVisiteProposte() {
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getVisite()) {
            if (v.getStatus() == StatusVisita.PROPOSTA)
                out.add(v);
        }

        return out;
    }

    public void checkVisiteInTerminazione(Date d) {
        for (Visita v : getVisite()) {
            v.mancano3Giorni(d);
            if (v.getStatus() == StatusVisita.CANCELLATA) {
                scriviVisiteCancellate(v);
                cachedVisite.remove(v.getUID());
            }
        }
    }

    private boolean scriviVisiteCancellate(Visita toAdd) {
        archivio.add(toAdd);
        return saveJson(archivio);
    }

    public ArrayList<Visita> getVisiteEffettuate() {
        return archivio;
    }

    public Visita getVisitaByUID(String uid) {
        return cachedVisite.get(uid);
    }
}
