package V3.Ingsoft.controller.item.persone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

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

    public Fruitore(String[] a) throws Exception {    //added with command
        this(a[0], a[1], true, true);
    }

    public ArrayList<String> getVisiteUIDs() {
        return this.visiteUIDs;
    }

    public void subscribeToVisit(String uid) {
        if (visiteUIDs.contains(uid))
            return;

        visiteUIDs.add(uid);
    }

    public void removeFromVisita(String uid) {
        visiteUIDs.remove(uid);
    }
}