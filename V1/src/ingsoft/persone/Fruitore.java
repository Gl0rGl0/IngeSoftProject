package ingsoft.persone;

public class Fruitore extends Persona {
    int numeroIscrizioni = 0;

    public Fruitore(String username, String psw, String nuovo) {
        super(username, psw, PersonaType.FRUITORE, nuovo);
    }

    public void setNumeroIscrizioni(int n){
        this.numeroIscrizioni = n;
    }

    public int getNumeroIscrizioni(){
        return this.numeroIscrizioni;
    }
}