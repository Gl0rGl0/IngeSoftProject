package V5.Ingsoft.controller.item.persone;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import V5.Ingsoft.controller.item.interfaces.Deletable;

public class Iscrizione extends Deletable{
    @JsonIgnore
    public static String PATH = "iscrizioni";
    private String fruitoreUID;
    private int quantity;

    @JsonCreator
    public Iscrizione(
        @JsonProperty("fUID") String fruitoreUID,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("UID") String uid ) throws Exception {
        super(uid);

        this.fruitoreUID = fruitoreUID;
        this.quantity = quantity;
    }

    public Iscrizione(String fruitoreUID, int quantity) throws Exception {
        super(UUID.randomUUID().toString().split("-")[0]);

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

    @Override
    public String toString() {
        return "ID=" + super.UID +
                "\nFruitore=" + fruitoreUID +
                "\nQuantity=" + quantity;
    }
}
