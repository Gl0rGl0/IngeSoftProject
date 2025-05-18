package V5.Ingsoft.controller.item.persone;

import java.util.UUID;

import V5.Ingsoft.controller.item.Informable;

public class Iscrizione implements Informable{
    private String fruitoreUID;
    private int quantity;
    private String UIDIscrizione;

    public Iscrizione(String fruitoreUID, int quantity) {
        this.fruitoreUID = fruitoreUID;
        this.quantity = quantity;

        this.UIDIscrizione = UUID.randomUUID().toString().split("-")[0];;
    }

    public String getUIDIscrizione() {
        return this.UIDIscrizione;
    }

    public String getUIDFruitore() {
        return this.fruitoreUID;
    }

    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public String toString() {
        return "ID=" + UIDIscrizione +
                "\nFruitore=" + fruitoreUID +
                "\nQuantity=" + quantity;
    }

    @Override
    public String getMainInformation() {
        return this.UIDIscrizione;
    }
}
