package V1.ingsoft.persone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuratore extends Persona {
    @JsonCreator
    public Configuratore(
            @JsonProperty("username") String username,
            @JsonProperty("psw") String psw,
            @JsonProperty("new") boolean nuovo) {
        super(username, psw, PersonaType.CONFIGURATORE, nuovo, false);
    }

    public Configuratore(String username, String psw, boolean nuovo, boolean hash) {
        super(username, psw, PersonaType.CONFIGURATORE, nuovo, hash);
    }
}