package ingsoft.persone;

public abstract class Persona {
    private String username;
    private String psw;

    public Persona(String username, String psw) {
        this.username = username;
        this.psw = psw;
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

    @Override
    public String toString() {
        return "Username: " + getUsername() + " - Password: *****";
    }
}