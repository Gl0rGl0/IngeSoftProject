package V5.Ingsoft.model;

import V5.Ingsoft.controller.item.interfaces.DBWithStatus;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import V5.Ingsoft.controller.item.luoghi.Visita;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.statuses.StatusVisita;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;

import java.util.ArrayList;
import java.util.List;

public class DBVisiteHelper extends DBMapHelper<Visita> implements DBWithStatus {

    public DBVisiteHelper() {
        super(Visita.PATH, Visita.class);
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
            AssertionControl.logMessage("Attempted to add null Visita or Visita with null/empty UID.", Payload.Status.ERROR, CLASSNAME);
            return;
        }
        if (cachedItems.containsKey(toAdd.getUID())) {
            AssertionControl.logMessage("Attempted to add Visita with duplicate UID: " + toAdd.getUID(), Payload.Status.WARN, CLASSNAME);
            return;
        }
        cachedItems.put(toAdd.getUID(), toAdd);
        // Consider saving immediately or batching saves
        // saveDB();
    }

    /**
     * Removes a Visita from the cache and saves the update, based on its UID.
     *
     * @param uid the UID of the visita to remove
     */
    public void removeVisitaByUID(String uid) {
        final String CLASSNAME = DBVisiteHelper.class.getSimpleName() + ".removeVisitaByUID";
        if (uid == null || uid.trim().isEmpty()) {
            AssertionControl.logMessage("Attempted to remove Visita with null/empty UID.", Payload.Status.ERROR, CLASSNAME);
            return;
        }
        if (cachedItems.containsKey(uid)) {
            cachedItems.remove(uid);
            boolean success = saveDB(); // Save after removal
            if (!success) {
                AssertionControl.logMessage("Failed to save JSON after removing Visita UID: " + uid, Payload.Status.ERROR, CLASSNAME);
            }
        } else {
            AssertionControl.logMessage("Attempted to remove non-existent Visita UID: " + uid, Payload.Status.WARN, CLASSNAME);
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
            AssertionControl.logMessage("Attempted to find Visita with null/empty title or data string.", Payload.Status.WARN, CLASSNAME);
            return null;
        }

        Date d;
        try {
            d = new Date(data);
        } catch (Exception e) {
            AssertionControl.logMessage("Error while parsing the date.", Payload.Status.WARN, CLASSNAME);
            return null;
        }

        for (Visita v : getItems()) {
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

    public ArrayList<Visita> getVisiteComplete() {
        return getVisitaByStatus(StatusVisita.COMPLETED);
    }

    public ArrayList<Visita> getVisiteConfermate() {
        return getVisitaByStatus(StatusVisita.CONFIRMED);
    }

    public ArrayList<Visita> getVisiteCancellate() {
        return getVisitaByStatus(StatusVisita.CANCELLED);
    }

    public ArrayList<Visita> getVisiteProposte() {
        return getVisitaByStatus(StatusVisita.PROPOSED);
    }

    public ArrayList<Visita> getVisiteEffettuate() {
        return getVisitaByStatus(StatusVisita.PERFORMED);
    }

    public ArrayList<Visita> getVisitaByStatus(StatusVisita sv) {
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : getItems()) {
            if (v.getStatus() == sv)
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
    @Override
    public void checkItems(Date d) {
        final String CLASSNAME = DBVisiteHelper.class.getSimpleName() + ".checkVisite";
        if (d == null) {
            AssertionControl.logMessage("Current date 'd' cannot be null.", Payload.Status.WARN, CLASSNAME);
            return;
        }

        ArrayList<String> uidsToRemove = new ArrayList<>();
        boolean changed = false;

        // Creiamo una copia delle visite correnti per evitare modifiche concorrenti
        ArrayList<Visita> currentVisits = new ArrayList<>(cachedItems.values());

        for (Visita v : currentVisits) {
            if (v == null) {
                AssertionControl.logMessage("Null Visita found in cache during daily check.", Payload.Status.ERROR, CLASSNAME);
                continue;
            }

            Date visitDate = v.getDate();
            if (visitDate == null) {
                AssertionControl.logMessage("Visita with UID " + v.getUID() + " has null date.", Payload.Status.ERROR, CLASSNAME);
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
            saveDB(); // Salva cambiamenti alla cache
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
        Date visitDate = v.getDate();
        Date deadlineDate = visitDate.clone().minusDays(3);

        // Se non siamo ancora arrivati al termine delle iscrizioni, esci subito
        if (currentDay.isBefore(deadlineDate)) {
            return false;
        }

        // Considera solo visite in stato PROPOSED (o COMPLETED se serve davvero)
        StatusVisita currentStatus = v.getStatus();
        if (currentStatus != StatusVisita.PROPOSED) {
            return false;
        }

        // Controlla la presenza del tipo di visita
        TipoVisita tv = v.getTipoVisita();
        if (tv == null) {
            AssertionControl.logMessage(
                    "Visita UID " + v.getUID() + " has null TipoVisita.",
                    Payload.Status.ERROR, className
            );
            return false;
        }

        // Conferma o cancella in base ai partecipanti
        boolean hasEnough = v.getCurrentNumber() >= tv.getNumMinPartecipants();
        finalizeVisit(v, hasEnough, className);

        return true;
    }

    private void finalizeVisit(Visita v, boolean hasEnoughParticipants, String className) {
        if (hasEnoughParticipants) {
            v.setStatus(StatusVisita.CONFIRMED);
            AssertionControl.logMessage("Visita UID " + v.getUID() + " confirmed.", Payload.Status.INFO, className);
        } else {
            v.setStatus(StatusVisita.CANCELLED);
            AssertionControl.logMessage("Visita UID " + v.getUID() + " cancelled (insufficient participants).", Payload.Status.INFO, className);
        }
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

        Date visitDate = v.getDate().clone().addDay(1); //the day next the visit
        if (visitDate.equals(currentDay)) {
            StatusVisita currentStatus = v.getStatus();

            if (currentStatus == StatusVisita.CONFIRMED) {
                v.setStatus(StatusVisita.COMPLETED);
                uidsToRemove.add(v.getUID());
                AssertionControl.logMessage("Visita UID " + v.getUID() + " marked COMPLETED and archived.", Payload.Status.INFO, className);
                changed = true;
            } else if (currentStatus == StatusVisita.CANCELLED) {
                uidsToRemove.add(v.getUID());
                AssertionControl.logMessage("Cancelled Visita UID " + v.getUID() + " removed after date passed.", Payload.Status.INFO, className);
                changed = true;
            }

        }

        return changed;
    }

    public void close() {
        // Save the archive before closing
        saveDB();
    }

    public List<Visita> getVisiteByVolontarioAndData(String usernameV, Date d) {
        final String CLASSNAME = DBVisiteHelper.class.getSimpleName() + ".getVisiteByVolontarioAndData";
        List<Visita> out = new ArrayList<>();
        if (usernameV == null || usernameV.trim().isEmpty() || d == null) {
            AssertionControl.logMessage("Attempted getVisiteByVolontarioAndData with null/empty username or null date.", Payload.Status.ERROR, CLASSNAME);
            return out; // Return empty list
        }

        for (Visita v : getItems()) {
            // Add null checks for safety
            if (v != null && v.getVolontarioUID() != null && v.getDate() != null &&
                    v.getVolontarioUID().equals(usernameV) && v.getDate().equals(d))
                out.add(v);
        }
        return out;
    }

    public boolean volontarioHaConflitto(Volontario v, Date data, TipoVisita t) {
        final String CLASSNAME = DBVisiteHelper.class.getSimpleName() + ".volontarioHaConflitto";
        if (v == null || data == null || t == null || v.getUsername() == null || t.getInitTime() == null) {
            AssertionControl.logMessage("Null argument provided to volontarioHaConflitto.", Payload.Status.WARN, CLASSNAME);
            return true; // Treat null input as a conflict to be safe? Or return false? Returning true seems safer.
        }

        List<Visita> visiteGiornaliere = this.getVisiteByVolontarioAndData(v.getUsername(), data);
        int initTime = t.getInitTime().getMinutes();
        int duration = t.getDuration();
        for (Visita visita : visiteGiornaliere) {
            // Add null checks for safety
            if (visita == null || visita.getTipoVisita() == null || visita.getTipoVisita().getInitTime() == null) {
                AssertionControl.logMessage("Encountered null Visita or related data during conflict check for Volontario: " + v.getUsername(), Payload.Status.WARN, CLASSNAME);
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
