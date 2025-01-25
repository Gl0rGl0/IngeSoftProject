package ingsoft.persone;

public class Configuratore extends Persona {
    public Configuratore(String username, String psw) {
        super(username, psw);
    }

    public String toString() {
        return "Username: " + getUsername() + " - Password: " + getPsw();
    }
}