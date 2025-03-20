package V1.ingsoft.controller.item.persone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuratore extends Persona {
    @JsonCreator
    public Configuratore(
            @JsonProperty("username") String username,
            @JsonProperty("psw") String psw,
            @JsonProperty("new") boolean isNew) {
        super(username, psw, PersonaType.CONFIGURATORE, isNew, false);
    }

    public Configuratore(String username, String psw, boolean isNew, boolean hash) {
        super(username, psw, PersonaType.CONFIGURATORE, isNew, hash);
    }
}