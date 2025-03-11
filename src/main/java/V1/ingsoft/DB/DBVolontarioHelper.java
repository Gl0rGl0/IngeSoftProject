package V1.ingsoft.DB;

import V1.ingsoft.persone.PersonaType;
import V1.ingsoft.persone.Volontario;

public class DBVolontarioHelper extends DBAbstractPersonaHelper<Volontario> {
    public DBVolontarioHelper() {
        super(PersonaType.VOLONTARIO, Volontario.class);
    }
}