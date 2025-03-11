package V1.ingsoft.persone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuratore extends Persona {
    @JsonCreator
    public Configuratore(
            @JsonProperty("username") String username,
            @JsonProperty("psw") String psw,
            @JsonProperty("new") boolean nuovo) {
        super(username, psw, PersonaType.CONFIGURATORE, nuovo);
    }
}