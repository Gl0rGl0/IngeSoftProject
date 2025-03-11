package V1.ingsoft.luoghi;

import V1.ingsoft.util.GPS;

import java.util.ArrayList;
import java.util.UUID;

public class Luogo {
    String nomeLuogo;
    String descrizioneLuogo;
    GPS posizione;
    String UID;
    ArrayList<String> visiteUID = new ArrayList<>();

    public Luogo(String nomeLuogo, String descrizioneLuogo, GPS posizione, String UID) {
        this.nomeLuogo = nomeLuogo;
        this.descrizioneLuogo = descrizioneLuogo;
        this.posizione = posizione;
        this.UID = UID;
    }

    public Luogo(String nomeLuogo, String descrizioneLuogo, GPS posizione) {
        this.nomeLuogo = nomeLuogo;
        this.descrizioneLuogo = descrizioneLuogo;
        this.posizione = posizione;
        this.UID = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Luogo {")
                .append("Nome='").append(nomeLuogo).append('\'')
                .append(", Descrizione='").append(descrizioneLuogo).append('\'')
                .append(", Posizione=").append(posizione)
                .append(", Visite=[");

        if (visiteUID != null && !visiteUID.isEmpty()) {
            visiteUID.forEach(visita -> {
                sb.append("\n    ").append(visita).append(",");
            });
            sb.setLength(sb.length() - 1); // Rimuove l'ultima virgola
            sb.append("\n");
        }

        sb.append("]}");
        return sb.toString();
    }

    public void aggiungiTipoVisita(String tvUID) {
        this.visiteUID.add(tvUID);
    }

    public void removeTipoVisita(String tvUID) {
        this.visiteUID.remove(tvUID);
    }

    public ArrayList<String> getTipoVisitaUID() {
        return this.visiteUID;
    }

    public String getNome() {
        return this.nomeLuogo;
    }

    public String getDescrizione() {
        return this.descrizioneLuogo;
    }

    public GPS getGps() {
        return this.posizione;
    }

    public String getUID() {
        return this.UID;
    }
}