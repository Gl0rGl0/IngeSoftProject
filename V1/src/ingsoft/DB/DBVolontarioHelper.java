package ingsoft.DB;

import ingsoft.persone.PersonaType;
import ingsoft.persone.Volontario;

public class DBVolontarioHelper extends DBAbstractPersonaHelper<Volontario> {
    public DBVolontarioHelper() {
        super(PersonaType.VOLONTARIO, Volontario.class);
    }
}