package V1.ingsoft.persone;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import V1.ingsoft.DB.DBAbstractPersonaHelper;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public abstract class Persona {
    private String psw;
    private final String username;
    private boolean isNew;

    @JsonIgnore
    public final PersonaType personaType;
    @JsonIgnore
    protected ArrayList<String> visiteUIDs = new ArrayList<>();

    public Persona(String username, String psw, PersonaType personaType, boolean isNew) {
        this(username, psw, PersonaType.VOLONTARIO, isNew, true);
    }

    public Persona(String username, String psw, PersonaType personaType, boolean isNew, boolean hash) {
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

    @JsonIgnore // Jackson is[qualcosa] lo salva come [qualcosa]: return value...
    public boolean isNew() {
        return this.isNew;
    }

    public void setAsNotNew() {
        this.isNew = false;
    }

    public PersonaType getType() {
        return this.personaType;
    }

    // se si disattiva non si potranno vedere le psw del database dall'app (come
    // giusto che sia)
    @JsonIgnore
    boolean testPsw = true;

    @Override
    public String toString() {
        if (testPsw)
            return "Username: " + getUsername() + " - Password: " + getPsw() + " - Nuovo: " + isNew();
        // return "Username: " + getUsername() + " - Password: " + getPsw();
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