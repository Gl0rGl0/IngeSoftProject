package ingsoft.persone;

public abstract class Persona {
    
    private String psw;
    private final String username;
    private final PersonaType personaType;
    private boolean nuovo;

    public Persona(String username, String psw, PersonaType personaType, String nuovo) {
        this.username = username;
        this.psw = psw;
        this.personaType = personaType;

        this.nuovo = nuovo.equals("1");
    }

    public String getUsername() {
        return this.username;
    }

    public void notNew(){
        this.nuovo = false;
    }

    public String getPsw() {
        return this.psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getNew(){
        return this.nuovo ? "0" : "1";
    }

    public boolean firstAccess(){
        return this.nuovo;
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

    public int getPriorita() {
        return this.personaType.getPriorita();
    }

}