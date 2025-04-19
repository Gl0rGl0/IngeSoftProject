package V3.Ingsoft.model;

import V3.Ingsoft.controller.item.persone.Fruitore;
import V3.Ingsoft.controller.item.persone.PersonaType;

public class DBFruitoreHelper extends DBAbstractPersonaHelper<Fruitore> {
    public DBFruitoreHelper() {
        super(PersonaType.FRUITORE);
    }

    public void addFruitore(Fruitore f) {
        if (cachedItems.get(f.getUsername()) != null)
            return;

        cachedItems.put(f.getUsername(), f);
        saveJson(getPersonList());
    }
}