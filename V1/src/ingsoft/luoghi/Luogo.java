package ingsoft.luoghi;

import ingsoft.util.GPS;
import java.util.ArrayList;

public class Luogo {
    String nomeLuogo;
    String descrizioneLuogo;
    GPS posizione;
    ArrayList<Visita> visite;

    public Luogo(String nomeLuogo, String descrizioneLuogo, GPS posizione, ArrayList<Visita> visite) {
        this.nomeLuogo = nomeLuogo;
        this.descrizioneLuogo = descrizioneLuogo;
        this.posizione = posizione;
        this.visite = visite;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Luogo {")
        .append("Nome='").append(nomeLuogo).append('\'')
        .append(", Descrizione='").append(descrizioneLuogo).append('\'')
        .append(", Posizione=").append(posizione)
        .append(", Visite=[");

        if (visite != null && !visite.isEmpty()) {
            for (Visita visita : visite) {
                sb.append("\n    ").append(visita.toString()).append(",");
            }
            sb.setLength(sb.length() - 1); // Rimuove l'ultima virgola
            sb.append("\n");
        }

        sb.append("]}");
        return sb.toString();
    }

}