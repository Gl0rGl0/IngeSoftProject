package V4.Ingsoft.controller.item.persone;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import V4.Ingsoft.util.Date;
import V4.Ingsoft.view.ViewSE;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Volontario extends Persona {

    private final ArrayList<String> UIDvisitePresentabili = new ArrayList<>();

    public void addTipoVisita(String uidTipoVisita) {
        if (!UIDvisitePresentabili.contains(uidTipoVisita)) {
            UIDvisitePresentabili.add(uidTipoVisita);
        }
    }

    public void removeUIDVisita(String uidTipoVisita) {
        UIDvisitePresentabili.remove(uidTipoVisita);
    }

    public ArrayList<String> getTipiVisiteUIDs() {
        return this.UIDvisitePresentabili;
    }

    public static final String PATH = "volontari";

    public boolean[] availability = new boolean[31];
    @JsonIgnore
    public int numAvailability;
    @JsonIgnore
    private boolean valid = false;

    @JsonCreator
    public Volontario(
            @JsonProperty("username") String username,
            @JsonProperty("psw") String psw,
            @JsonProperty("new") boolean isNew,
            @JsonProperty("visite") ArrayList<String> visite,
            @JsonProperty("disponibilita") boolean[] disponibilita) {
        this(username, psw, isNew);
        this.visiteUIDs = visite;
        if (disponibilita != null)
            this.availability = disponibilita;
    }

    public Volontario(String username, String psw, boolean isNew, boolean hash) {
        super(username, psw, PersonaType.VOLONTARIO, isNew, hash);
    }

    public Volontario(String username, String psw, boolean isNew) {
        this(username, psw, isNew, false);
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
    public void setAvailability(Date oggi, Date disp) {
        if (!twoMonthsDifference(oggi, disp)) {
            ViewSE.println("Errore, non puoi inserire una data al di fuori del mese successivo");
            return;
        }

        valid = false;
        availability[disp.getDay()] = true;
    }

    private boolean twoMonthsDifference(Date d1, Date d2) {
        int currentMonth = d1.getMonth();
        int targetMonth = d2.getMonth();

        if (d1.getDay() < 16) {
            return (currentMonth + 2) == targetMonth;
        } else {
            return (currentMonth + 3) == targetMonth;
        }
    }

    public void clearAvailability() {
        availability = new boolean[31];
    }

    /**
     * Metodo per stampare le disponibilità per il debug.
     */
    public String getAvailabilityString() {
        return (Arrays.toString(availability));
    }

    public boolean[] getAvailability() {
        return availability;
    }

    public int getNumAvailability() {
        if (valid) {
            return numAvailability;
        }

        for (boolean b : availability) {
            if (b) {
                numAvailability += 1;
            }
        }

        valid = true;
        return numAvailability;
    }
}
