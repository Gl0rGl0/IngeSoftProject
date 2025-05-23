package V5.Ingsoft.model;

import V5.Ingsoft.controller.item.interfaces.Product;
import V5.Ingsoft.controller.item.persone.Fruitore;
import V5.Ingsoft.controller.item.persone.PersonaType;

public class DBFruitoreHelper extends DBAbstractPersonaHelper<Fruitore> implements Product{
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