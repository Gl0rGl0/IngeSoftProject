package V4.Ingsoft.model;

import V4.Ingsoft.controller.item.persone.Fruitore;
import V4.Ingsoft.controller.item.persone.PersonaType;

public class DBFruitoreHelper extends DBAbstractPersonaHelper<Fruitore> {
    public DBFruitoreHelper() {
        super(PersonaType.FRUITORE);
    }
}