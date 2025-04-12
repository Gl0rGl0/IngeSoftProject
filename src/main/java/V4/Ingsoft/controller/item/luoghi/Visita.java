package V4.Ingsoft.controller.item.luoghi;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import V4.Ingsoft.controller.item.persone.Fruitore;
import V4.Ingsoft.controller.item.persone.Iscrizione;
import V4.Ingsoft.util.Date;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class Visita {
    @JsonIgnore
    public static final String PATH = "archivio-visite";

    @JsonIgnore
    public TipoVisita tipo;
    public Date date;
    public String UID;

    @JsonIgnore
    public String volontarioUID;
    public StatusVisita status = StatusVisita.PROPOSED; // Updated enum constant
    @JsonIgnore
    public ArrayList<Iscrizione> fruitori = new ArrayList<>();

    @JsonCreator
    public Visita(
            @JsonProperty("data") Date data,
            @JsonProperty("UID") String UID,
            @JsonProperty("stato") StatusVisita status) {

        this.date = data;
        this.UID = UID;
        this.status = status;
    }

    public Visita(TipoVisita tipo, Date date, String uidVolontario) {
        this.tipo = tipo;
        this.date = date;
        this.volontarioUID = uidVolontario;
        this.UID = tipo.getUID() + date.hashCode() + uidVolontario;
    }

    public StatusVisita getStatus() {
        return this.status;
    }

    public String getUidVolontario() {
        return this.volontarioUID;
    }

    public String getUID() {
        return this.UID;
    }

    public String tipoVisitaUID() {
        return this.tipo.getUID();
    }

    public TipoVisita getTipoVisita() {
        return tipo;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return tipo.getTitle();
    }

    public void setStatus(StatusVisita status) {
        this.status = status;
    }

    public boolean hasFruitore(String userF) {
        for (Iscrizione i : fruitori) {
            if (i.fruitoreUID.equals(userF))
                return true;
        }
        return false;
    }

    public String addPartecipants(Fruitore f, int n) {
        if (tipo.numMaxPartecipants - getCurrentNumber() < n) {
            return "capacity"; // Indicates full capacity
        }

        if (hasFruitore(f.getUsername())) {
            return "present"; // Indicates user already registered
        }

        Iscrizione newIscrizione = new Iscrizione(f.getUsername(), n);
        fruitori.add(newIscrizione);

        if (getCurrentNumber() == tipo.numMaxPartecipants) {
            setStatus(StatusVisita.COMPLETED); // Updated enum constant
        }
        return newIscrizione.getUIDFruitore();
    }

    /**
     * Removes the first Iscrizione associated with the given user username.
     * Updates status to PROPOSED if the visit was previously full.
     * @param user The username of the fruitore whose booking should be removed.
     * @return true if a participant was found and removed, false otherwise.
     */
    public boolean removePartecipant(String user) {
        int capienzaAttuale = getCurrentNumber();
        for (Iscrizione i : fruitori) {
            // Ensure null safety for user comparison
            if (user != null && user.equals(i.getUIDFruitore())) {
                boolean removed = fruitori.remove(i);
                if (removed && capienzaAttuale == tipo.getNumMaxPartecipants() && this.status == StatusVisita.COMPLETED) {
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

    public void isThreeDaysFrom(Date d) {
        if (date.dayOfTheYear() - d.dayOfTheYear() <= 3) {
            if (getCurrentNumber() < this.tipo.numMinPartecipants) {
                this.status = StatusVisita.CANCELLED; // Updated enum constant
            } else {
                this.status = StatusVisita.CONFIRMED; // Updated enum constant
            }
        }
    }

    @Override
    public String toString() {
        return "Title: " + tipo.getTitle()
                + "\nVolunteer: " + getUidVolontario()
                + "\nDescription: " + tipo.getDescription()
                + "\nMeeting Point: " + tipo.getMeetingPlace()
                + "\nDate: " + date
                + "\nTime: " + tipo.getInitTime()
                + "\nTicket required: " + tipo.isFree() // Assuming 'Biglietto necessario' means 'Ticket required'
                + "\n";
    }

    public ArrayList<Iscrizione> getIscrizioni() {
        return fruitori;
    }

    /**
     * Removes an Iscrizione from this Visita based on the Iscrizione's UID.
     * Also handles potential status change if the visit was full.
     * @param uidIscrizione The UID of the Iscrizione to remove.
     * @return true if an Iscrizione was removed, false otherwise.
     */
    public boolean removeIscrizioneByUID(String uidIscrizione) {
        int capienzaAttuale = getCurrentNumber();
        boolean removed = fruitori.removeIf(iscrizione -> iscrizione.getUIDIscrizione().equals(uidIscrizione));

        // If an iscrizione was removed and the visit was previously full (COMPLETED),
        // it should become PROPOSED again.
        if (removed && capienzaAttuale == tipo.getNumMaxPartecipants() && this.status == StatusVisita.COMPLETED) {
             setStatus(StatusVisita.PROPOSED);
        }
        return removed;
    }
}
