package V1.ingsoft.DB;

import V1.ingsoft.persone.Fruitore;
import V1.ingsoft.persone.PersonaType;

public class DBFruitoreHelper extends DBAbstractPersonaHelper<Fruitore> {
    public DBFruitoreHelper() {
        super(PersonaType.FRUITORE, Fruitore.class);
    }
}