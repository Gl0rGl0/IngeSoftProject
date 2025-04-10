package V4.Ingsoft.controller.item.persone;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Fruitore extends Persona {

    @JsonCreator
    public Fruitore(
            @JsonProperty("username") String username,
            @JsonProperty("psw") String psw,
            @JsonProperty("new") boolean isNew) throws Exception {
        this(username, psw, isNew, false);
    }

    public Fruitore(String username, String psw, boolean isNew, boolean hash) throws Exception {
        super(username, psw, PersonaType.FRUITORE, isNew, hash);
    }

    public Fruitore(String[] a) throws Exception{    //added with command
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