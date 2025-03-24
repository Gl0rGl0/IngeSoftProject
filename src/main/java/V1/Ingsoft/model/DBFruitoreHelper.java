package V1.Ingsoft.model;

import V1.Ingsoft.controller.item.persone.Fruitore;
import V1.Ingsoft.controller.item.persone.PersonaType;

public class DBFruitoreHelper extends DBAbstractPersonaHelper<Fruitore> {
    public DBFruitoreHelper() {
        super(PersonaType.FRUITORE);
    }
}