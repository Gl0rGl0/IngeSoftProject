package ingsoft.persone;

import java.util.UUID;

public class Iscrizione {
    public String fruitoreUID;
    public int q = 0;
    public String UIDIscrizione;

    public Iscrizione(String fruitoreUID, int quantita){
        this.fruitoreUID = fruitoreUID;
        this.q = quantita;

        this.UIDIscrizione = UUID.randomUUID().toString();
    }

    public String getUIDIscrizione(){
        return this.UIDIscrizione;
    }

    public String getUIDFruitore(){
        return this.fruitoreUID;
    }

    public void setNumeroIscrizioni(int n){
        this.q = n;
    }

    public int getNumeroIscrizioni(){
        return this.q;
    }
}
