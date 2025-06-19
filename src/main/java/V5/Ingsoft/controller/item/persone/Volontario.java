package V5.Ingsoft.controller.item.persone;

import V5.Ingsoft.controller.item.statuses.StatusItem;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Volontario extends Persona {
    public static final String PATH = "volontari";

    private ArrayList<String> visiteAssignedUIDs = new ArrayList<>();
    private boolean[] availability = new boolean[31];

    @JsonCreator
    public Volontario(
            @JsonProperty("username") String username,
            @JsonProperty("psw") String psw,
            @JsonProperty("new") boolean isNew,
            @JsonProperty("visite") ArrayList<String> visite,
            @JsonProperty("disponibilita") boolean[] disponibilita,
            @JsonProperty("status") StatusItem status,
            @JsonProperty("insertionDate") Date insertionDate,
            @JsonProperty("deletionDate") Date deletionDate) throws Exception {
        this(username, psw, isNew, false);
        this.visiteAssignedUIDs = visite;
        if (disponibilita != null)
            this.availability = disponibilita;
        this.si = status;
        this.insertionDate = insertionDate;
        this.deletionDate = deletionDate;
    }

    public Volontario(String username, String psw, boolean isNew, boolean hash) throws Exception {
        super(username, psw, PersonaType.VOLONTARIO, isNew, hash);
    }

    public Volontario(String[] a, Date d) throws Exception {
        this(a[0], a[1], true, true);
        this.insertionDate = d;
        si = StatusItem.PENDING_ADD;
    }

    public boolean addTipoVisita(String uidTipoVisita) {
        if (!visiteAssignedUIDs.contains(uidTipoVisita)) {
            return visiteAssignedUIDs.add(uidTipoVisita);
        }
        return false;
    }

    public boolean removeTipoVisita(String uidTipoVisita) {
        return this.visiteAssignedUIDs.remove(uidTipoVisita);
    }

    public ArrayList<String> getTipivisiteAssignedUIDs() { return this.visiteAssignedUIDs; }

    /**
     * Imposta la disponibilità per una data specifica nel periodo updatebile.
     * La data (disp) deve appartenere al periodo updatebile, cioè: - Se
     * disp.getMonth() corrisponde a modifiablePeriodStartMonth, il day deve
     * essere ≥ 16. - Se disp.getMonth() corrisponde al month successivo, il
     * day deve essere ≤ 15. Viene calcolato un indice in base a questo
     * mapping e si imposta lo slot a true.
     *
     * @param oggi la data corrente (per aggiornare eventualmente il periodo)
     * @param disp la data per cui si vuole impostare la disponibilità
     */
    public Payload.Status setAvailability(Date oggi, Date disp, boolean toAdd) {
        if (Date.monthsDifference(oggi, disp) != 1) {
            System.out.println("Errore, non puoi inserire una data al di fuori del mese successivo");
            return Payload.Status.ERROR;
        }

        availability[disp.getDay() - 1] = toAdd;
        AssertionControl.logMessage("Set availability: " + this.getUsername() + "Date: " + disp + " to: " + toAdd, Payload.Status.INFO, "Volontario.setAvailability");
        return Payload.Status.INFO;
    }

    public void clearAvailability()      { availability = new boolean[31]; }
    public boolean[] getAvailability()   { return availability; }
    public boolean isAvailable(int day) { return availability[day - 1]; }

    public int getNAvailability() {
        int n = 0;
        for (boolean available : getAvailability())
            if (available) n++;
        return n;
    }

}
