package V4.Ingsoft.model;

import V4.Ingsoft.controller.item.StatusVisita;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.persone.Volontario;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;

import java.util.ArrayList;
import java.util.List;

public class DBVisiteHelper extends DBAbstractHelper<Visita> {
    private final ArrayList<Visita> archivio = new ArrayList<>();

    public DBVisiteHelper() {
        super(Visita.PATH, Visita.class);

        archivio.addAll(getJson());
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
     * @param toAdd la visita da aggiungere
     */
    public void addVisita(Visita toAdd) {
        final String CLASSNAME = DBVisiteHelper.class.getSimpleName() + ".addVisita";
        if (toAdd == null || toAdd.getUID() == null || toAdd.getUID().trim().isEmpty()) {
            AssertionControl.logMessage("Attempted to add null Visita or Visita with null/empty UID.", 1, CLASSNAME);
            return;
        }
        if (cachedItems.containsKey(toAdd.getUID())) {
            AssertionControl.logMessage("Attempted to add Visita with duplicate UID: " + toAdd.getUID(), 2, CLASSNAME);
            return;
        }
        cachedItems.put(toAdd.getUID(), toAdd);
        // Consider saving immediately or batching saves
        // saveJson();
    }

    /**
     * Removes a Visita from the cache and saves the update, based on its UID.
     *
     * @param uid the UID of the visita to remove
     */
    public void removeVisitaByUID(String uid) {
        final String CLASSNAME = DBVisiteHelper.class.getSimpleName() + ".removeVisitaByUID";
        if (uid == null || uid.trim().isEmpty()) {
            AssertionControl.logMessage("Attempted to remove Visita with null/empty UID.", 1, CLASSNAME);
            return;
        }
        if (cachedItems.containsKey(uid)) {
            cachedItems.remove(uid);
            boolean success = saveJson(); // Save after removal
            if (!success) {
                AssertionControl.logMessage("Failed to save JSON after removing Visita UID: " + uid, 1, CLASSNAME);
            }
        } else {
            AssertionControl.logMessage("Attempted to remove non-existent Visita UID: " + uid, 2, CLASSNAME);
        }
    }

    /**
     * Cerca e restituisce una Visita in cache in base al name.
     *
     * @param title il name della visita da cercare
     * @return la Visita trovata, oppure null se non esiste.
     */
    public Visita findVisita(String title, String data) {
        final String CLASSNAME = DBVisiteHelper.class.getSimpleName() + ".findVisita";
        if (title == null || title.trim().isEmpty() || data == null || data.trim().isEmpty()) {
            AssertionControl.logMessage("Attempted to find Visita with null/empty title or data string.", 2, CLASSNAME);
            return null;
        }

        Date d;
        try {
            d = new Date(data);
        } catch (Exception e) {
            AssertionControl.logMessage("Error while parsing the date.", 2, CLASSNAME);
            return null;
        }

        for (Visita v : getVisite()) {
            // Add null checks for safety inside the loop
            if (v != null &&
                    v.getTitle() != null &&
                    v.getDate() != null &&
                    v.getTitle().equalsIgnoreCase(title) &&
                    v.getDate().equals(d)) {
                return v;
            }
        }
        return null;
    }

    public ArrayList<Visita> getCompletate() {
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getVisite()) {
            if (v.getStatus() == StatusVisita.COMPLETED)
                out.add(v);
        }

        return out;
    }

    public ArrayList<Visita> getConfermate() {
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getVisite()) {
            if (v.getStatus() == StatusVisita.CONFIRMED)
                out.add(v);
        }

        return out;
    }

    public ArrayList<Visita> getVisiteCancellate() {
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getVisite()) {
            if (v.getStatus() == StatusVisita.CANCELLED)
                out.add(v);
        }

        return out;
    }

    public ArrayList<Visita> getVisiteProposte() {
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getVisite()) {
            if (v.getStatus() == StatusVisita.PROPOSED)
                out.add(v);
        }

        return out;
    }

    /**
     * Checks and updates the status of visits based on the current date 'd'.
     * Handles confirmation/cancellation 3 days before the visit date,
     * and completion/removal after the visit date.
     *
     * @param d The current simulated date.
     */
    public void checkVisite(Date d) {
        final String CLASSNAME = DBVisiteHelper.class.getSimpleName() + ".checkVisite";
        if (d == null) {
            AssertionControl.logMessage("Current date 'd' cannot be null.", 2, CLASSNAME);
            return;
        }

        ArrayList<String> uidsToRemove = new ArrayList<>();
        boolean changed = false;

        // Creiamo una copia delle visite correnti per evitare modifiche concurrenti
        ArrayList<Visita> currentVisits = new ArrayList<>(cachedItems.values());

        for (Visita v : currentVisits) {
            if (v == null) {
                AssertionControl.logMessage("Null Visita found in cache during daily check.", 1, CLASSNAME);
                continue;
            }

            Date visitDate = v.getDate();
            if (visitDate == null) {
                AssertionControl.logMessage("Visita with UID " + v.getUID() + " has null date.", 1, CLASSNAME);
                continue;
            }

            // Fase 1: Aggiornamento visit pre-deadline
            if (processPreDeadlineVisit(v, d, CLASSNAME)) {
                changed = true;
            }

            // Fase 2: Pulizia post-visita (visite passate)
            if (processPostVisitCleanup(v, d, uidsToRemove, CLASSNAME)) {
                changed = true;
            }
        }

        // Rimuovi le visite dalla cache per cui la data è passata o cancellate
        for (String uid : uidsToRemove) {
            cachedItems.remove(uid);
        }

        if (changed || !uidsToRemove.isEmpty()) {
            saveJson(); // Salva cambiamenti alla cache
        }
    }

    /**
     * Gestisce l'aggiornamento di una visita nella fase pre-deadline (3 giorni prima della visita).
     * Se la data corrente coincide con il deadline e se il numero di partecipanti raggiunge il minimo,
     * la visita viene confermata, altrimenti annullata.
     *
     * @param v          La visita da processare.
     * @param currentDay La data corrente.
     * @param className  Il nome della classe per il logging.
     * @return true se lo stato della visita è stato aggiornato, false altrimenti.
     */
    private boolean processPreDeadlineVisit(Visita v, Date currentDay, String className) {
        boolean changed = false;

        Date visitDate = v.getDate();
        Date deadlineDate = visitDate.minusDays(3);
        if (deadlineDate != null &&
                Math.abs(deadlineDate.dayOfTheYear() - currentDay.dayOfTheYear()) <= 3) {
            StatusVisita currentStatus = v.getStatus();
            if (currentStatus == StatusVisita.PROPOSED || currentStatus == StatusVisita.COMPLETED) {
                TipoVisita tv = v.getTipoVisita();
                if (tv == null) {
                    AssertionControl.logMessage("Visita UID " + v.getUID() + " has null TipoVisita.", 1, className);
                    return changed;
                }
                int minParticipants = tv.getNumMinPartecipants();
                int currentParticipants = v.getCurrentNumber();

                if (currentParticipants >= minParticipants) {
                    v.setStatus(StatusVisita.CONFIRMED);
                    AssertionControl.logMessage("Visita UID " + v.getUID() + " confirmed.", 4, className);
                } else {
                    v.setStatus(StatusVisita.CANCELLED);
                    AssertionControl.logMessage("Visita UID " + v.getUID() + " cancelled (insufficient participants).", 3, className);
                }
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Gestisce la pulizia post-visita. Se la data della visita è precedente alla data corrente,
     * la visita viene considerata per l'eliminazione. Se la visita era CONFIRMED, viene segnato come COMPLETED
     * e archiviato; se CANCELLED, viene rimossa.
     *
     * @param v            La visita da processare.
     * @param currentDay   La data corrente.
     * @param uidsToRemove Lista degli UID da rimuovere dalla cache.
     * @param className    Il nome della classe per il logging.
     * @return true se la visita è stata processata (modifica o marcatura per rimozione), false altrimenti.
     */
    private boolean processPostVisitCleanup(Visita v, Date currentDay, ArrayList<String> uidsToRemove, String className) {
        boolean changed = false;

        Date visitDate = v.getDate();
        if (visitDate.isBefore(currentDay)) {
            StatusVisita currentStatus = v.getStatus();
            if (currentStatus == StatusVisita.CONFIRMED) {
                v.setStatus(StatusVisita.COMPLETED);
                writeVisitaEffettuata(v);
                uidsToRemove.add(v.getUID());
                AssertionControl.logMessage("Visita UID " + v.getUID() + " marked COMPLETED and archived.", 4, className);
                changed = true;
            } else if (currentStatus == StatusVisita.CANCELLED) {
                uidsToRemove.add(v.getUID());
                AssertionControl.logMessage("Cancelled Visita UID " + v.getUID() + " removed after date passed.", 4, className);
                changed = true;
            }
        }

        return changed;
    }

    /**
     * Adds a completed visit to the archive and saves the archive.
     *
     * @param toAdd The visit to archive.
     */
    private void writeVisitaEffettuata(Visita toAdd) {
        archivio.add(toAdd);
        saveJson(archivio);
    }

    public ArrayList<Visita> getVisiteEffettuate() {
        return archivio;
    }

    public Visita getVisitaByUID(String uid) {
        final String CLASSNAME = DBVisiteHelper.class.getSimpleName() + ".getVisitaByUID";
        if (uid == null || uid.trim().isEmpty()) {
            AssertionControl.logMessage("Attempted to get Visita with null/empty UID.", 1, CLASSNAME);
            return null;
        }
        return cachedItems.get(uid);
    }

    public void close() {
        // Save the archive before closing
        saveJson(archivio);
    }

    public List<Visita> getVisiteByVolontarioAndData(String usernameV, Date d) {
        final String CLASSNAME = DBVisiteHelper.class.getSimpleName() + ".getVisiteByVolontarioAndData";
        List<Visita> out = new ArrayList<>();
        if (usernameV == null || usernameV.trim().isEmpty() || d == null) {
            AssertionControl.logMessage("Attempted getVisiteByVolontarioAndData with null/empty username or null date.", 1, CLASSNAME);
            return out; // Return empty list
        }

        for (Visita v : getVisite()) {
            // Add null checks for safety
            if (v != null && v.getUidVolontario() != null && v.getDate() != null &&
                    v.getUidVolontario().equals(usernameV) && v.getDate().equals(d))
                out.add(v);
        }
        return out;
    }

    public boolean volontarioHaConflitto(Volontario v, Date data, TipoVisita t) {
        final String CLASSNAME = DBVisiteHelper.class.getSimpleName() + ".volontarioHaConflitto";
        if (v == null || data == null || t == null || v.getUsername() == null || t.getInitTime() == null) {
            AssertionControl.logMessage("Null argument provided to volontarioHaConflitto.", 2, CLASSNAME);
            return true; // Treat null input as a conflict to be safe? Or return false? Returning true seems safer.
        }

        List<Visita> visiteGiornaliere = this.getVisiteByVolontarioAndData(v.getUsername(), data);
        int initTime = t.getInitTime().getMinutes();
        int duration = t.getDuration();
        for (Visita visita : visiteGiornaliere) {
            // Add null checks for safety
            if (visita == null || visita.getTipoVisita() == null || visita.getTipoVisita().getInitTime() == null) {
                AssertionControl.logMessage("Encountered null Visita or related data during conflict check for Volontario: " + v.getUsername(), 2, CLASSNAME);
                continue; // Skip this potentially corrupt visit data
            }
            int initTimeV = visita.getTipoVisita().getInitTime().getMinutes();
            int durationV = visita.getTipoVisita().getDuration();
            // Check for overlap: (StartA < EndB) and (StartB < EndA)
            if ((initTimeV < initTime + duration) && (initTime < initTimeV + durationV)) {
                return true;
            }
        }
        return false;
    }
}
