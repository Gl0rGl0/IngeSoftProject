package V1.Ingsoft.controller.item.persone;

import java.util.UUID;

public class Iscrizione {
    public String fruitoreUID;
    public int quantity = 0;
    public String UIDIscrizione;

    public Iscrizione(String fruitoreUID, int quantity) {
        this.fruitoreUID = fruitoreUID;
        this.quantity = quantity;

        this.UIDIscrizione = UUID.randomUUID().toString();
    }

    public String getUIDIscrizione() {
        return this.UIDIscrizione;
    }

    public String getUIDFruitore() {
        return this.fruitoreUID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return this.quantity;
    }
}
