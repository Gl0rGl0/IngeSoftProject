package V4.Ingsoft.controller.item.persone;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;
import V4.Ingsoft.view.ViewSE;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Volontario extends Persona {

    private final ArrayList<String> UIDvisitePresentabili = new ArrayList<>();

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


    public static final String PATH = "volontari";

    public boolean[] availability = new boolean[31];

    @JsonCreator
    public Volontario(
            @JsonProperty("username") String username,
            @JsonProperty("psw") String psw,
            @JsonProperty("new") boolean isNew,
            @JsonProperty("visite") ArrayList<String> visite,
            @JsonProperty("disponibilita") boolean[] disponibilita) throws Exception {
        this(username, psw, isNew, false);
        this.visiteUIDs = visite;
        if (disponibilita != null)
            this.availability = disponibilita;
    }

    public Volontario(String username, String psw, boolean isNew, boolean hash) throws Exception {
        super(username, psw, PersonaType.VOLONTARIO, isNew, hash);
    }

    public Volontario(String[] a) throws Exception{
        this(a[0], a[1], true, true);
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
        if (!Date.monthsDifference(oggi, disp, 1)) {
            ViewSE.println("Errore, non puoi inserire una data al di fuori del mese successivo");
            return;
        }

        availability[disp.getDay() - 1] = toAdd;
        AssertionControl.logMessage("Set availability: " + this.getUsername() + "Date: " + disp + " to: " + toAdd,3, "Volontario.setAvailability");
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

    public int getNAvailability(){
        int n = 0;
        for (boolean b : getAvailability()) {
            if(b)
                n++;
        }
        return n;
    }

    public boolean isAvailabile(int day) {
        return availability[day-1];
    }
}
