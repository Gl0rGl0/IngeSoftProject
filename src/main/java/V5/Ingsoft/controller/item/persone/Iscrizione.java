package V5.Ingsoft.controller.item.persone;

import V5.Ingsoft.controller.item.interfaces.Deletable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.util.UUID;

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

    public String getUIDFruitore() {
        return this.fruitoreUID;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getMainInformation() {
        return super.UID;
    }

    public String getVisitaUID() {
        return visitaUID;
    }

    @Override
    public String toString() {
        return "ID=" + super.UID +
                "\nFruitore=" + fruitoreUID +
                "\nQuantity=" + quantity;
    }
}
