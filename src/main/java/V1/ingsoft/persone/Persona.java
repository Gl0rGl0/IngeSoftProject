package V1.ingsoft.persone;

import java.util.ArrayList;

public abstract class Persona {

    private String psw;
    private final String username;
    private final PersonaType personaType;
    private boolean nuovo;

    protected ArrayList<String> uidVisitePartecipante = new ArrayList<>();

    public Persona(String username, String psw, PersonaType personaType, String nuovo) {
        this.username = username;
        this.psw = psw;
        this.personaType = personaType;

        this.nuovo = nuovo.equals("1");
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

    public String getNew() { // come firstAccess ma in stringa...
        return this.nuovo ? "1" : "0";
    }

    public boolean firstAccess() {
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
    boolean testPsw = true;

    @Override
    public String toString() {
        if (testPsw)
            return "Username: " + getUsername() + " - Password: " + getPsw();
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