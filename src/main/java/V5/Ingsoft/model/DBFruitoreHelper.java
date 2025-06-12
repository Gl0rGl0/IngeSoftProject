package V5.Ingsoft.model;

import java.util.ArrayList;

import V5.Ingsoft.controller.item.persone.Fruitore;
import V5.Ingsoft.controller.item.persone.PersonaType;

public class DBFruitoreHelper extends DBAbstractPersonaHelper<Fruitore> {
    public DBFruitoreHelper() {
        super(PersonaType.FRUITORE);
    }

    public DBFruitoreHelper(ArrayList<Fruitore> list) {
        super(PersonaType.FRUITORE, list);
    }

    public boolean addFruitore(Fruitore f) {
        return super.addItem(f);
    }
}