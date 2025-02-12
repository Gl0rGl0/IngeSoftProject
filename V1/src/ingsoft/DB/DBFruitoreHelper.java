package ingsoft.DB;

import ingsoft.persone.Fruitore;
import ingsoft.persone.PersonaType;

public class DBFruitoreHelper extends DBAbstractPersonaHelper<Fruitore> {
    public DBFruitoreHelper() {
        super(PersonaType.FRUITORE, Fruitore.class);
    }
}