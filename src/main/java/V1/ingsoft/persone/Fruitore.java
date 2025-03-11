package V1.ingsoft.persone;

import java.util.ArrayList;

public class Fruitore extends Persona {

    public Fruitore(String username, String psw, String nuovo) {
        super(username, psw, PersonaType.FRUITORE, nuovo);
    }

    @Override
    public ArrayList<String> getVisiteIscrittoUID() {
        return this.uidVisitePartecipante;
    }

    @Override
    public boolean iscriviVisita(String uid) {
        if (uidVisitePartecipante.contains(uid))
            return false;

        return uidVisitePartecipante.add(uid);
    }

    @Override
    public boolean disiscriviVisita(String uid) {
        return uidVisitePartecipante.remove(uid);
    }
}