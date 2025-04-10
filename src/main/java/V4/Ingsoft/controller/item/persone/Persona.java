package V4.Ingsoft.controller.item.persone;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import V4.Ingsoft.model.DBAbstractPersonaHelper;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public abstract class Persona {
    private String psw;
    private final String username;
    private boolean isNew;

    @JsonIgnore
    public final PersonaType personaType;
    @JsonIgnore
    protected ArrayList<String> visiteUIDs = new ArrayList<>();

    public Persona(String username, String psw, PersonaType personaType, boolean isNew) throws Exception {
        this(username, psw, PersonaType.VOLONTARIO, isNew, true);
    }

    public Persona(String username, String psw, PersonaType personaType, boolean isNew, boolean hash) throws Exception{
        if(username == null || username.isBlank())
            throw new Exception("Error in " + personaType + " constructor: Username can't be empty");

        if(psw == null || psw.isEmpty())
            throw new Exception("Error in " + personaType + " constructor: Password can't be empty");

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
        return "Username: " + getUsername() + " - Password: *****";
    }

    public int getPriority() {
        return this.personaType.getPriority();
    }

    public ArrayList<String> getVisiteUIDs() {
        return null;
    }

    public boolean subscribeToVisit(String uid) {
        return false;
    }

    public boolean removeFromVisita(String uid) {
        return false;
    }
}
