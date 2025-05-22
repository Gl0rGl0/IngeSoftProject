package V5.Ingsoft.controller.item.persone;

import V5.Ingsoft.controller.item.interfaces.Deletable;
import V5.Ingsoft.model.DBAbstractPersonaHelper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public abstract class Persona extends Deletable {
    @JsonIgnore
    private final PersonaType personaType;
    private final String username;
    @JsonIgnore
    protected ArrayList<String> visiteUIDs = new ArrayList<>();
    private String psw;
    private boolean isNew;

    public Persona(String username, String psw, PersonaType personaType, boolean isNew, boolean hash) throws Exception {
        if (username == null || username.isBlank())
            throw new Exception("Username can't be empty");

        if (psw == null || psw.isEmpty())
            throw new Exception("Password can't be empty");

        this.username = username;
        this.psw = hash ? DBAbstractPersonaHelper.securePsw(username, psw) : psw;
        this.personaType = personaType;
        this.isNew = isNew;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPsw() {
        return this.psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    @JsonIgnore // Jackson saves is[something] as [something]: return value...
    public boolean isNew() {
        return this.isNew;
    }

    public void setAsNotNew() {
        this.isNew = false;
    }

    public PersonaType getType() {
        return this.personaType;
    }

    @Override
    public String toString() {
        return "Username: " + getUsername();
    }

    public int getPriority() {
        return this.personaType.getPriority();
    }

    @Override
    public String getMainInformation(){
        return this.username;
    }
}
