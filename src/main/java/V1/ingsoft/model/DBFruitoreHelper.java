package V1.ingsoft.model;

import V1.ingsoft.controller.item.persone.Fruitore;
import V1.ingsoft.controller.item.persone.PersonaType;

public class DBFruitoreHelper extends DBAbstractPersonaHelper<Fruitore> {
    public DBFruitoreHelper() {
        super(PersonaType.FRUITORE);
    }
}