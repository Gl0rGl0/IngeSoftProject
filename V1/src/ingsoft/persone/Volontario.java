package ingsoft.persone;

public class Volontario extends Persona{
    public Volontario(String username, String psw, String nuovo) {
        super(username, psw, PersonaType.VOLONTARIO, nuovo);
    }
}