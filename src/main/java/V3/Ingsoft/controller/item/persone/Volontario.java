package V3.Ingsoft.controller.item.persone;

import V3.Ingsoft.controller.item.StatusItem;
import V3.Ingsoft.util.AssertionControl;
import V3.Ingsoft.util.Date;
import V3.Ingsoft.view.ViewSE;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Volontario extends Persona {
    public static final String PATH = "volontari";

    private final ArrayList<String> UIDvisitePresentabili = new ArrayList<>();
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
        this.visiteUIDs = visite;
        if (disponibilita != null)
            this.availability = disponibilita;
        this.si = status;
        this.insertionDate = insertionDate;
        this.deletionDate = deletionDate;
    }

    private Volontario(String username, String psw, boolean isNew, boolean hash) throws Exception {
        super(username, psw, PersonaType.VOLONTARIO, isNew, hash);
    }

    public Volontario(String[] a, Date d) throws Exception {
        this(a[0], a[1], true, true);
        this.insertionDate = d;
    }

    public boolean addTipoVisita(String uidTipoVisita) {
        if (!UIDvisitePresentabili.contains(uidTipoVisita)) {
            return UIDvisitePresentabili.add(uidTipoVisita);
        }
        return false;
    }

    public void removeUIDVisita(String uidTipoVisita) {
        UIDvisitePresentabili.remove(uidTipoVisita);
    }

    public ArrayList<String> getTipiVisiteUIDs() {
        return this.UIDvisitePresentabili;
    }

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
    public void setAvailability(Date oggi, Date disp, boolean toAdd) {
        if (Date.monthsDifference(oggi, disp) > 1) {
            ViewSE.println("Errore, non puoi inserire una data al di fuori del mese successivo");
            return;
        }

        availability[disp.getDay() - 1] = toAdd;
        AssertionControl.logMessage("Set availability: " + this.getUsername() + "Date: " + disp + " to: " + toAdd, 3, "Volontario.setAvailability");
    }

    public void clearAvailability() {
        availability = new boolean[31];
    }

    public boolean[] getAvailability() {
        return availability;
    }

    public int getNAvailability() {
        int n = 0;
        for (boolean b : getAvailability()) {
            if (b)
                n++;
        }
        return n;
    }

    public boolean isAvailable(int day) {
        return availability[day - 1];
    }
}
