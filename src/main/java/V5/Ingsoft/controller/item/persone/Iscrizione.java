package V5.Ingsoft.controller.item.persone;

import V5.Ingsoft.controller.item.interfaces.Deletable;
import V5.Ingsoft.controller.item.interfaces.Storageble;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.util.Objects; // Per equals/hashCode
import java.util.UUID; // Per UID robusto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class Iscrizione extends Deletable {
    @JsonIgnore
    public final static String PATH = "iscrizioni";
    private final String fruitoreUID;
    private final int quantity;
    private final String visitaUID;

    @JsonCreator
    public Iscrizione(
            @JsonProperty("vUID") String visitaUID,
            @JsonProperty("fUID") String fruitoreUID,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("UID") String uid) throws Exception {
        super(uid);

        if (visitaUID == null || visitaUID.isEmpty() || fruitoreUID == null || fruitoreUID.isEmpty() || quantity <= 0)
            throw new IllegalArgumentException("Iscrizione cannot have null/empty UIDs or zero/negative quantity.");

        this.visitaUID = visitaUID;
        this.fruitoreUID = fruitoreUID;
        this.quantity = quantity;
    }

    public Iscrizione(String visitaUID, String fruitoreUID, int quantity) throws Exception {
        super(UUID.randomUUID().toString().split("-")[0]);

        this.visitaUID = visitaUID;
        this.fruitoreUID = fruitoreUID;
        this.quantity = quantity;
    }

    public String getUIDFruitore()      { return this.fruitoreUID; }
    public int getQuantity()            { return this.quantity; }
    public String getMainInformation()  { return super.UID; }
    public String getVisitaUID()        { return visitaUID; }

    @Override
    public String toString() {
        return "ID=" + super.UID +
                "\nFruitore=" + fruitoreUID +
                "\nQuantity=" + quantity +
                "\nVisita UID=" + visitaUID;
    }

    public boolean equals(Storageble o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.UID.equals(o.getUID());
    }

    @Override
    public int hashCode() { return Objects.hash(this.UID); }
}