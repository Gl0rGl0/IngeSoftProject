package V1.ingsoft.luoghi;

import V1.ingsoft.DB.DBAbstractPersonaHelper;
// import V1.ingsoft.persone.PersonaType;
import V1.ingsoft.util.GPS;

import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class Luogo {
    @JsonIgnore
    public static final String PATH = "luoghi";

    String nomeLuogo;
    String descrizioneLuogo;
    GPS posizione;
    String UID;
    ArrayList<String> visiteUID = new ArrayList<>();

    @JsonCreator
    public Luogo(
            @JsonProperty("nomeLuogo") String nomeLuogo,
            @JsonProperty("descrizioneLuogo") String descrizioneLuogo,
            @JsonProperty("posizione") GPS posizione,
            @JsonProperty("UID") String UID) {
        this.nomeLuogo = nomeLuogo;
        this.descrizioneLuogo = descrizioneLuogo;
        this.posizione = posizione;
        this.UID = UID;
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAA\n\n\n\n\n");
    }

    public Luogo(
            String nomeLuogo,
            String descrizioneLuogo,
            GPS posizione) {
        this.nomeLuogo = nomeLuogo;
        this.descrizioneLuogo = descrizioneLuogo;
        this.posizione = posizione;
        this.UID = nomeLuogo.hashCode() + "";
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