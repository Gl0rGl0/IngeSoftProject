package V5.Ingsoft.controller.item.real;

import V5.Ingsoft.controller.item.interfaces.Deletable;
import V5.Ingsoft.controller.item.persone.Iscrizione;
import V5.Ingsoft.controller.item.statuses.Result;
import V5.Ingsoft.controller.item.statuses.StatusVisita;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Date;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class Visita extends Deletable {
    @JsonIgnore
    public static final String PATH = "visite";

    private final Date date;
    private final String tipoVisitaUID;
    private final String volontarioUID;
    private final ArrayList<Iscrizione> fruitori = new ArrayList<>();
    private StatusVisita status = StatusVisita.PROPOSED; // Updated enum constant

    @JsonCreator
    public Visita(
            @JsonProperty("data") Date data,
            @JsonProperty("UID") String UID,
            @JsonProperty("tvUID") String tVUID,
            @JsonProperty("volUID") String volUID,
            @JsonProperty("stato") StatusVisita status) throws Exception {
        super(UID);

        this.tipoVisitaUID = tVUID;
        this.volontarioUID = volUID;
        this.date = data;
        this.status = status;
    }

    public Visita(TipoVisita tipo, Date date, String uidVolontario) throws Exception {
        super(String.format("%s-%s-%s", tipo.getUID(), date.toString(), uidVolontario).hashCode() + "v");

        this.tipoVisitaUID = tipo.getUID();
        this.volontarioUID = uidVolontario;
        this.date = date;
    }

    public StatusVisita getStatus() { return this.status; }
    public void setStatus(StatusVisita status) { this.status = status; }
    public String getVolontarioUID() { return this.volontarioUID; }
    public String getTipoVisitaUID() { return this.tipoVisitaUID; }
    public Date getDate() { return date; }
    public String getTitle() { return getTipoVisita().getTitle(); }

    public TipoVisita getTipoVisita() {
        return Model.getInstance().dbTipoVisiteHelper.getItem(this.tipoVisitaUID);
    }

    public boolean hasFruitore(String userF) {
        for (Iscrizione i : fruitori) {
            if (i.getUIDFruitore().equals(userF))
                return true;
        }
        return false;
    }

    public Result addPartecipants(Iscrizione i) {
        int max = getTipoVisita().getNumMaxPartecipants();

        if (max - getCurrentNumber() < i.getQuantity()) {
            return Result.NOTENOUGH_CAPACITY; // Indicates not enough capacity
        }

        if (max - getCurrentNumber() == 0) {
            return Result.FULL_CAPACITY; // Indicates full capacity
        }

        if (hasFruitore(i.getUIDFruitore())) {
            return Result.ALREADY_SIGNED; // Indicates user already registered
        }

        fruitori.add(i);

        if (getCurrentNumber() == max) {
            setStatus(StatusVisita.COMPLETED); // Updated enum constant
        }
        return Result.SUCCESS;
    }

    /**
     * Removes the first Iscrizione associated with the given user username.
     * Updates status to PROPOSED if the visit was previously full.
     *
     * @param user The username of the fruitore whose booking should be removed.
     * @return true if a participant was found and removed, false otherwise.
     */
    public boolean removePartecipant(String user) {
        int max = getTipoVisita().getNumMaxPartecipants();

        int capienzaAttuale = getCurrentNumber();
        for (Iscrizione i : fruitori) {
            // Ensure null safety for user comparison
            if (user != null && user.equals(i.getUIDFruitore())) {
                boolean removed = fruitori.remove(i);
                if (removed && capienzaAttuale == max && this.status == StatusVisita.COMPLETED) {
                    // If removed and was full, set back to proposed
                    setStatus(StatusVisita.PROPOSED);
                }
                return removed; // Return true if removal was successful
            }
        }
        return false; // Return false if no matching participant was found
    }

    public int getCurrentNumber() {
        int out = 0;
        for (Iscrizione i : fruitori) {
            out += i.getQuantity();
        }
        return out;
    }

    @Override
    public String toString() {
        TipoVisita tv = getTipoVisita();

        return "Title: " + tv.getTitle()
                + "\n\tDescription: " + tv.getDescription()
                + "\n\tMeeting Point: " + tv.getMeetingPlace()
                + "\n\tDate: " + date
                + "\n\tTime: " + tv.getInitTime()
                + "\n\tTicket required: " + tv.isFree()
                + "\n\tVolunteer: " + getVolontarioUID()
                + "\n";
    }

    public ArrayList<Iscrizione> getIscrizioni() {
        return fruitori;
    }

    /**
     * Removes an Iscrizione from this Visita based on the Iscrizione's UID.
     * Also handles potential status change if the visit was full.
     *
     * @param uidIscrizione The UID of the Iscrizione to remove.
     */
    public void removeIscrizioneByUID(String uidIscrizione) {
        int capienzaAttuale = getCurrentNumber();
        boolean removed = fruitori.removeIf(iscrizione -> iscrizione.getUID().equals(uidIscrizione));

        // If an iscrizione was removed and the visit was previously full (COMPLETED),
        // it should become PROPOSED again.
        if (removed && capienzaAttuale == getTipoVisita().getNumMaxPartecipants() && this.status == StatusVisita.COMPLETED) {
            setStatus(StatusVisita.PROPOSED);
        }
    }

    @Override
    public String getMainInformation() {
        return this.getTitle();
    }
}
