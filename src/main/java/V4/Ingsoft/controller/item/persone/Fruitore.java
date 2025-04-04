package V4.Ingsoft.controller.item.persone;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Fruitore extends Persona {

    @JsonCreator
    public Fruitore(
            @JsonProperty("username") String username,
            @JsonProperty("psw") String psw,
            @JsonProperty("new") boolean isNew) {
        this(username, psw, isNew, false);
    }

    private Fruitore(String username, String psw, boolean isNew, boolean hash) {
        super(username, psw, PersonaType.FRUITORE, isNew, hash);
    }

    public Fruitore(String[] a){    //added with command
        this(a[0], a[1], true, true);
    }

    @Override
    public ArrayList<String> getVisiteUIDs() {
        return this.visiteUIDs;
    }

    @Override
    public boolean subscribeToVisit(String uid) {
        if (visiteUIDs.contains(uid))
            return false;

        return visiteUIDs.add(uid);
    }

    @Override
    public boolean removeFromVisita(String uid) {
        return visiteUIDs.remove(uid);
    }
}