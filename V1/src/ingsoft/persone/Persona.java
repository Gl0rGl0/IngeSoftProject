package ingsoft.persone;

public abstract class Persona {
    private String username;
    private String psw;
    private PersonaType personaType;
    private boolean firstAccess = true;

    public Persona(String username, String psw, PersonaType personaType) {
        this.username = username;
        this.psw = psw;
        this.personaType = personaType;
    }

    public String getUsername() {
        return username;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public PersonaType type(){
        return this.personaType;
    }

    boolean testPsw = true;
    @Override
    public String toString() {
        if(testPsw)
            return "Username: " + getUsername() + " - Password: " + getPsw();
        return "Username: " + getUsername() + " - Password: *****";
    }

}