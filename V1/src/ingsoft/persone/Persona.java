package ingsoft.persone;

public abstract class Persona {
    private String username;
    private String psw;
    private PersonaType personaType;

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

    // @Override
    // public String toString() {
    //     return "Username: " + getUsername() + " - Password: *****";
    // }

    @Override
    public String toString() {
        return "Username: " + getUsername() + " - Password: " + getPsw();
    }
}