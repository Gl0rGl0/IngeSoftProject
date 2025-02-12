package ingsoft.DB;

import ingsoft.persone.Volontario;
import ingsoft.persone.PersonaType;

public class DBVolontarioHelper extends DBAbstractPersonaHelper<Volontario> {
    public DBVolontarioHelper() {
        super(PersonaType.VOLONTARIO, Volontario.class);
    }
}