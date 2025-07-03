package V4.Ingsoft.controller.item.persone;

import java.util.UUID;

public class Iscrizione {
    public String fruitoreUID;
    public int quantity;
    public String UIDIscrizione;

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
}
