package V1.ingsoft.persone;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public abstract class Persona {
    private String psw;
    private final String username;
    private boolean nuovo;

    @JsonIgnore
    public final PersonaType personaType;
    @JsonIgnore
    protected ArrayList<String> uidVisitePartecipante = new ArrayList<>();

    public Persona(String username, String psw, PersonaType personaType, boolean nuovo) {
        this.username = username;
        this.psw = psw;
        this.personaType = personaType;
        this.nuovo = nuovo;
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

    public boolean getNew() {
        return this.nuovo;
    }

    public void notNew() {
        this.nuovo = false;
    }

    public PersonaType type() {
        return this.personaType;
    }

    // se si disattiva non si potranno vedere le psw del database dall'app (come
    // giusto che sia)
    @JsonIgnore
    boolean testPsw = true;

    @Override
    public String toString() {
        if (testPsw)
            return "Username: " + getUsername() + " - Password: " + getPsw() + " - Nuovo: " + getNew();
        // return "Username: " + getUsername() + " - Password: " + getPsw();
        return "Username: " + getUsername() + " - Password: *****";
    }

    public int getPriorita() {
        return this.personaType.getPriorita();
    }

    public ArrayList<String> getVisiteIscrittoUID() {
        return null;
    }

    public boolean iscriviVisita(String uid) {
        return false;
    }

    public boolean disiscriviVisita(String uid) {
        return false;
    }
}