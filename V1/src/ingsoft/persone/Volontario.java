package ingsoft.persone;

public class Volontario extends Persona{
    public Volontario(String username, String psw, String n) {
        super(username, psw, PersonaType.VOLONTARIO, n);
    }
}