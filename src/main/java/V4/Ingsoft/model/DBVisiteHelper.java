package V4.Ingsoft.model;

import java.util.ArrayList;
import java.util.List;

import V4.Ingsoft.controller.item.luoghi.StatusVisita;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.persone.Volontario;
import V4.Ingsoft.util.Date;

public class DBVisiteHelper extends DBAbstractHelper<Visita> {
    private ArrayList<Visita> archivio = new ArrayList<>();

    public DBVisiteHelper() {
        super(Visita.PATH, Visita.class);

        getJson().forEach(v -> archivio.add(v));
    }

    /**
     * Legge il file delle proprietà e restituisce la lista delle visite.
     * Simile a getPersonList() in DBAbstractPersonaHelper, ma adattato per Visita.
     */
    public ArrayList<Visita> getVisite() {
        return super.getItems();
    }

    /**
     * Aggiunge una nuova Visita nel file delle proprietà.
     * Simile a addPersona(), ma adattato per Visita.
     *
     * @param visita la visita da aggiungere
     * @return true se l'aggiunta è andata a buon fine, false altrimenti.
     */
    public void addVisita(Visita toAdd) {
        cachedItems.put(toAdd.getUID(), toAdd);
    }

    /**
     * Rimuove una Visita dal file delle proprietà, basandosi sul name.
     * Simile a removePersona(), ma adattato per Visita.
     *
     * @param name il name della visita da rimuovere
     * @return true se la visita è stata rimossa, false in caso di errori.
     */
    public void removeVisita(String name, String date) {
        Visita toRemove = findVisita(name, date);
        if (toRemove == null)
            return;
        cachedItems.remove(toRemove.getUID());
    }

    /**
     * Cerca e restituisce una Visita in cache in base al name.
     *
     * @param name il name della visita da cercare
     * @return la Visita trovata, oppure null se non esiste.
     */
    public Visita findVisita(String title, String data) {
        for (Visita v : cachedItems.values()) {
            if (v.getTitle().equalsIgnoreCase(title) && v.getDate().toString().equals(data)) {
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

    public ArrayList<Visita> getVisiteCancellate() {
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
            v.isThreeDaysFrom(d);
            if (v.getStatus() == StatusVisita.CANCELLATA) {
                writeVisiteCancellate(v);
                cachedItems.remove(v.getUID());
            }
        }
    }

    private boolean writeVisiteCancellate(Visita toAdd) {
        archivio.add(toAdd);
        return saveJson(archivio);
    }

    public ArrayList<Visita> getVisiteEffettuate() {
        return archivio;
    }

    public Visita getVisitaByUID(String uid) {
        return cachedItems.get(uid);
    }

    public void close() {
        saveJson(archivio);
    }

    public List<Visita> getVisiteByVolontarioAndData(String usernameV, Date d) {
        List<Visita> out = new ArrayList<>();

        for (Visita v : getVisite()) {
            if (v.getUidVolontario().equals(usernameV) && v.getDate().equals(d))
                out.add(v);
        }
        return out;
    }

    public boolean volontarioHaConflitto(Volontario v, Date data, TipoVisita t) {
        List<Visita> visiteGiornaliere = this.getVisiteByVolontarioAndData(v.getUsername(), data);
        int initTime = t.getInitTime().getMinutes();
        int duration = t.getDuration();
        for (Visita visita : visiteGiornaliere) {
            int initTimeV = visita.getTipoVisita().getInitTime().getMinutes();
            int durationV = visita.getTipoVisita().getDuration();
            if (beetwen(initTimeV, initTime, initTimeV + durationV)
                    || beetwen(initTime, initTimeV, initTime + duration)) {
                return true;
            }
        }
        return false;
    }

    private boolean beetwen(int a, int b, int c) {
        return a < b && b < c;
    }
}
