package V1.ingsoft.persone;

import V1.ingsoft.util.Date;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

public class Volontario extends Persona {
    public void aggiungiTipoVisita(String uidTipoVisita) {
        if (!UIDvisitePresentabili.contains(uidTipoVisita)) {
            UIDvisitePresentabili.add(uidTipoVisita);
        }
    }

    public void removeUIDVisita(String uidTipoVisita) {
        UIDvisitePresentabili.remove(uidTipoVisita);
    }

    public ArrayList<String> getTipiVisiteUID() {
        return this.UIDvisitePresentabili;
    }

    public static final String PATH = "volontari";

    @JsonIgnore
    public boolean[] disponibilita = new boolean[31];
    @JsonIgnore
    public int numDisponibilita;
    @JsonIgnore
    private boolean valid = false;
    @JsonIgnore
    private final ArrayList<String> UIDvisitePresentabili = new ArrayList<>();

    @JsonCreator
    public Volontario(
            @JsonProperty("username") String username,
            @JsonProperty("psw") String psw,
            @JsonProperty("new") boolean nuovo) {
        super(username, psw, PersonaType.VOLONTARIO, nuovo, false);
    }

    public Volontario(String username, String psw, boolean nuovo, boolean hash) {
        super(username, psw, PersonaType.VOLONTARIO, nuovo, hash);
    }

    /**
     * Imposta la disponibilità per una data specifica nel periodo modificabile.
     * La data (disp) deve appartenere al periodo modificabile, cioè: - Se
     * disp.getMese() corrisponde a modifiablePeriodStartMonth, il giorno deve
     * essere ≥ 16. - Se disp.getMese() corrisponde al mese successivo, il
     * giorno deve essere ≤ 15. Viene calcolato un indice in base a questo
     * mapping e si imposta lo slot a true.
     *
     * @param oggi la data corrente (per aggiornare eventualmente il periodo)
     * @param disp la data per cui si vuole impostare la disponibilità
     */
    public void setDisponibilita(Date oggi, Date disp) {
        if (!separatiDaDueMesi(oggi, disp)) {
            System.out.println("DATA SBAGLIATA"); // TODO REMOVE
            return;
        }

        valid = false;
        disponibilita[disp.getGiorno()] = true;
    }

    private boolean separatiDaDueMesi(Date d1, Date d2) {
        int currentMonth = d1.getMese();
        int targetMonth = d2.getMese();

        if (d1.getGiorno() < 16) {
            return (currentMonth + 2) == targetMonth;
        } else {
            return (currentMonth + 3) == targetMonth;
        }
    }

    public void clearDisp() {
        disponibilita = new boolean[31];
    }

    /**
     * Metodo per stampare le disponibilità per il debug.
     */
    public String getDisponibilitaString() {
        return (Arrays.toString(disponibilita));
    }

    public boolean[] getDisponibilita() {
        return disponibilita;
    }

    public int getNumDisponibilita() {
        if (valid) {
            return numDisponibilita;
        }

        for (boolean b : disponibilita) {
            if (b) {
                numDisponibilita += 1;
            }
        }

        valid = true;
        return numDisponibilita;
    }
}
