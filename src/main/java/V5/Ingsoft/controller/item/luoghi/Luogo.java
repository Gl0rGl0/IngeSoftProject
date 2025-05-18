package V5.Ingsoft.controller.item.luoghi;

import V5.Ingsoft.controller.item.StatusItem;
import V5.Ingsoft.controller.item.Deletable;
import V5.Ingsoft.util.Date;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class Luogo extends Deletable {
    @JsonIgnore
    public static final String PATH = "luoghi";

    private final String name;
    private final String description;
    private final String position;
    private final String UID;
    private final ArrayList<String> visiteUID = new ArrayList<>();

    @JsonCreator
    public Luogo(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("position") String position,
            @JsonProperty("UID") String UID,
            @JsonProperty("status") StatusItem status,
            @JsonProperty("insertionDate") Date insertionDate,
            @JsonProperty("deletionDate") Date deletionDate) {
        this.name = name;
        this.description = description;
        this.position = position;
        this.UID = UID;
        this.si = status;
        this.insertionDate = insertionDate;
        this.deletionDate = deletionDate;
    }

    public Luogo(String[] a, Date d) throws Exception {
        if (a.length < 3)
            throw new Exception("Error in Luogo constructor: Insufficient number of arguments");

        if (a[0].isBlank())
            throw new Exception("Error in Luogo constructor: Title name can't be empty");

        if (a[1].isBlank())
            throw new Exception("Error in Luogo constructor: Description can't be empty");

        if (a[2].isBlank())
            throw new Exception("Error in Luogo constructor: Position can't be empty");

        this.name = a[0];
        this.description = a[1];
        this.position = a[2];
        this.UID = name.hashCode() + "l";

        this.insertionDate = d;
        this.si = StatusItem.PENDING_ADD;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Place {")
                .append("Name='").append(name).append('\'')
                .append(", Description='").append(description).append('\'')
                .append(", Position=").append(position)
                .append(", UID: ").append(UID)
                .append(", Status: ").append(si)
                .append(", Visits=[");

        if (!visiteUID.isEmpty()) {
            visiteUID.forEach(visita -> sb.append("\n    ").append(visita).append(","));
            sb.setLength(sb.length() - 1); // Removes the last comma
            sb.append("\n");
        }

        sb.append("]}");
        return sb.toString();
    }

    public boolean addTipoVisita(String tvUID) {
        if (visiteUID.contains(tvUID))
            return false;
        return this.visiteUID.add(tvUID);
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

    public String toArray() {
        return  "\"" + this.name + "\" " +
                "\"" + this.description + "\" " +
                "\"" + this.position + "\" ";
    }

    @Override
    public String getMainInformation() {
        return this.name;
    }
}
