package V1.ingsoft.persone;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Fruitore extends Persona {

    @JsonCreator
    public Fruitore(
            @JsonProperty("username") String username,
            @JsonProperty("psw") String psw,
            @JsonProperty("new") boolean nuovo) {
        super(username, psw, PersonaType.FRUITORE, nuovo, false);
    }

    public Fruitore(String username, String psw, boolean nuovo, boolean hash) {
        super(username, psw, PersonaType.FRUITORE, nuovo, hash);
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