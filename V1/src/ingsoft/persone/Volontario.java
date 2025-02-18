package ingsoft.persone;

import ingsoft.luoghi.StatusVisita;
import ingsoft.luoghi.Visita;
import ingsoft.util.Date;
import java.util.ArrayList;
import java.util.Arrays;

public class Volontario extends Persona {
    // Disponibilità: 
    // disponibilita[0] -> periodo chiuso (non modificabile)
    // disponibilita[1] -> periodo modificabile, definito dal cutoff (dal 16 del mese X al 15 del mese X+1)
    private final boolean[][] disponibilita = new boolean[2][31];
    
    // Memorizza il mese (0–11) del "periodo modificabile" (cioè il mese in cui partirà il periodo)
    private int modifiablePeriodStartMonth;
    
    // Il cutoff: il giorno 15 (il periodo inizia il giorno 16)
    private static final int CUTOFF = 15;
    
    private final ArrayList<Visita> visitePresentabili = new ArrayList<>();
    
    public void aggiungiVisita(Visita v){
        visitePresentabili.add(v);
    }

    public ArrayList<Visita> getVisite(StatusVisita s){
        ArrayList<Visita> out = new ArrayList<>();
        for (Visita v : visitePresentabili) {
            if(v.getStatus() == s)
                out.add(v);
        }
        return out;
    }

    /**
     * Costruttore.
     * È utile passare la data corrente per inizializzare correttamente il periodo modificabile.
     */
    public Volontario(String username, String psw, String nuovo) {
        super(username, psw, PersonaType.VOLONTARIO, nuovo);
    }
    
    /**
     * Dato oggi, restituisce il mese (0–11) a partire dal quale è modificabile la disponibilità.
     * Se oggi.getGiorno() ≤ 15, il periodo modificabile inizia il 16 dello stesso mese;
     * se oggi.getGiorno() ≥ 16, il periodo modificabile inizierà il 16 del mese successivo.
     */
    private int computeModifiablePeriodStartMonth(Date oggi) {
        if (oggi.getGiorno() <= CUTOFF) {
            return oggi.getMese();
        } else {
            return (oggi.getMese() + 1) % 12;
        }
    }
    
    /**
     * Aggiorna i periodi se la data corrente ha cambiato il periodo modificabile.
     * Se il mese calcolato in base a oggi differisce da quello memorizzato,
     * allora scorriamo i periodi: il vecchio periodo modificabile diventa chiuso e se ne crea uno nuovo.
     */
    public void aggiornaPeriodo(Date oggi) {
        int currentModPeriod = computeModifiablePeriodStartMonth(oggi);
        if (currentModPeriod != modifiablePeriodStartMonth) {
            scorriPeriodi();
            modifiablePeriodStartMonth = currentModPeriod;
        }
    }
    
    /**
     * Scorre i periodi:
     * - Il periodo modificabile diventa quello chiuso (disponibilita[0])
     * - Si alloca un nuovo array vuoto per il nuovo periodo modificabile (disponibilita[1])
     */
    private void scorriPeriodi() {
        disponibilita[0] = disponibilita[1];
        disponibilita[1] = new boolean[31];
    }
    
    /**
     * Imposta la disponibilità per una data specifica nel periodo modificabile.
     * La data (disp) deve appartenere al periodo modificabile, cioè:
     * - Se disp.getMese() corrisponde a modifiablePeriodStartMonth, il giorno deve essere ≥ 16.
     * - Se disp.getMese() corrisponde al mese successivo, il giorno deve essere ≤ 15.
     * Viene calcolato un indice in base a questo mapping e si imposta lo slot a true.
     * 
     * @param oggi la data corrente (per aggiornare eventualmente il periodo)
     * @param disp la data per cui si vuole impostare la disponibilità
     */
    public void setDisponibilita(Date oggi, Date disp) {
        // Aggiorna i periodi se necessario
        aggiornaPeriodo(oggi);
        
        int startMonth = modifiablePeriodStartMonth;
        int nextMonth = (startMonth + 1) % 12;
        
        int day = disp.getGiorno();
        int dispMonth = disp.getMese();
        
        int index;
        if (dispMonth == startMonth) {
            // Nel mese di inizio del periodo: accettiamo solo giorni >=16
            if (day <= CUTOFF) {
                throw new IllegalArgumentException("Nel mese di disponibilità il giorno deve essere >= " + (CUTOFF + 1));
            }
            index = day - (CUTOFF + 1);  // es: 16 diventa indice 0
        } else if (dispMonth == nextMonth) {
            // Nel mese successivo: accettiamo solo giorni <=15
            if (day > CUTOFF) {
                throw new IllegalArgumentException("Nel mese successivo il giorno deve essere <= " + CUTOFF);
            }
            // Calcoliamo la lunghezza della prima parte (dal 16 fino alla fine del mese di startMonth)
            // Per semplicità assumiamo che la lunghezza massima sia 31, dunque:
            int firstPartLength = 31 - CUTOFF; // ad esempio, 31 - 15 = 16 slot
            index = firstPartLength + day - 1; // es: giorno 1 -> indice firstPartLength
        } else {
            throw new IllegalArgumentException("La data non appartiene al periodo modificabile.");
        }
        
        if (index < 0 || index >= disponibilita[1].length) {
            throw new IllegalArgumentException("Indice calcolato fuori dai limiti (" + index + ").");
        }
        
        disponibilita[1][index] = true;
    }
    
    /**
     * Metodo per stampare le disponibilità per il debug.
     */
    public void printDisponibilita() {
        System.out.println(Arrays.toString(disponibilita[0]));
        System.out.println(Arrays.toString(disponibilita[1]));
    }
}