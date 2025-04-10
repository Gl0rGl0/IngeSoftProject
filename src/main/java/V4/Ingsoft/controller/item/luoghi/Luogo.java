package V4.Ingsoft.controller.item.luoghi;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import com.fasterxml.jackson.annotation.JsonCreator;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class Luogo {
    @JsonIgnore
    public static final String PATH = "luoghi";

    String name;
    String description;
    String position;
    String UID;
    ArrayList<String> visiteUID = new ArrayList<>();

    @JsonCreator
    public Luogo(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("position") String position,
            @JsonProperty("UID") String UID) {
        this.name = name;
        this.description = description;
        this.position = position;
        this.UID = UID;
    }

    public Luogo(String[] a) throws Exception{
        if(a.length < 3)
            throw new Exception("Error in Luogo constructor: Insufficient number of arguments");

        if(a[0].isBlank())
            throw new Exception("Error in Luogo constructor: Title name can't be empty");

        if(a[1].isBlank())
            throw new Exception("Error in Luogo constructor: Description can't be empty");

        if(a[2].isBlank())
            throw new Exception("Error in Luogo constructor: Position can't be empty");
        
        this.name = a[0];
        this.description = a[1];
        this.position = a[2];
        this.UID = name.hashCode() + "l";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Place {")
                .append("Name='").append(name).append('\'')
                .append(", Description='").append(description).append('\'')
                .append(", Position=").append(position)
                .append(", Visits=[");

        if (visiteUID != null && !visiteUID.isEmpty()) {
            visiteUID.forEach(visita -> {
                sb.append("\n    ").append(visita).append(",");
            });
            sb.setLength(sb.length() - 1); // Removes the last comma
            sb.append("\n");
        }

        sb.append("]}");
        return sb.toString();
    }

    public void addTipoVisita(String tvUID) {
        this.visiteUID.add(tvUID);
    }

    public void removeTipoVisita(String tvUID) {
        this.visiteUID.remove(tvUID);
    }

    public ArrayList<String> getTipoVisitaUID() {
        return this.visiteUID;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getMeetingPlace() {
        return this.position;
    }

    public String getUID() {
        return this.UID;
    }
}
