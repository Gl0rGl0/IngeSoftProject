package V5.Ingsoft.controller.item.persone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import V5.Ingsoft.controller.item.statuses.StatusItem;

public class Configuratore extends Persona {
    @JsonCreator
    public Configuratore(
            @JsonProperty("username") String username,
            @JsonProperty("psw") String psw,
            @JsonProperty("new") boolean isNew, 
            @JsonProperty("status") StatusItem status) throws Exception {
        super(username, psw, PersonaType.CONFIGURATORE, isNew, false);
    }

    public Configuratore(String username, String psw, boolean isNew, boolean hash) throws Exception {
        super(username, psw, PersonaType.CONFIGURATORE, isNew, hash);
    }

    public Configuratore(String[] a) throws Exception {
        this(a[0], a[1], true, true);
    }
}