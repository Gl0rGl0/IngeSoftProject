package ingsoft.persone;

import ingsoft.util.Date;
import ingsoft.util.ViewSE;
import java.util.ArrayList;
import java.util.Arrays;

public class Volontario extends Persona {

    // Disponibilità: 
    // disponibilita[0] -> periodo chiuso (non modificabile)
    // disponibilita[1] -> periodo modificabile, definito dal cutoff (dal 16 del mese X al 15 del mese X+1)
    public boolean[] disponibilita = new boolean[31];
    public int numDisponibilita;
    private boolean valid = false;

    private final ArrayList<String> UIDvisitePresentabili = new ArrayList<>();

    public void aggiungiTipoVisita(String uidTipoVisita) {
        if (!UIDvisitePresentabili.contains(uidTipoVisita)) {
            UIDvisitePresentabili.add(uidTipoVisita);
        }
    }

    public ArrayList<String> getTipiVisiteUID() {
        return this.UIDvisitePresentabili;
    }

    /**
     * Costruttore. È utile passare la data corrente per inizializzare
     * correttamente il periodo modificabile.
     */
    public Volontario(String username, String psw, String nuovo) {
        super(username, psw, PersonaType.VOLONTARIO, nuovo);
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
            return;
        }

        valid = false;
        disponibilita[disp.getGiorno()] = true;
    }

    private boolean separatiDaDueMesi(Date d1, Date d2) {
        int currentMonth = d2.getMese();
        int targetMonth = d1.getMese();

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
    public void printDisponibilita() {
        ViewSE.println(Arrays.toString(disponibilita));
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

    public void refreshGiornaliero(){
        
    }
}
