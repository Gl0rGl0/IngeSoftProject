package V1.ingsoft.persone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuratore extends Persona {
    @JsonCreator
    public Configuratore(
            @JsonProperty("userName") String userName,
            @JsonProperty("psw") String psw,
            @JsonProperty("new") boolean isNew) {
        super(userName, psw, PersonaType.CONFIGURATORE, isNew, false);
    }

    public Configuratore(String userName, String psw, boolean isNew, boolean hash) {
        super(userName, psw, PersonaType.CONFIGURATORE, isNew, hash);
    }
}