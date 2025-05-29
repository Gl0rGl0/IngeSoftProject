package V5.Ingsoft.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import V5.Ingsoft.controller.item.interfaces.Product;
import V5.Ingsoft.model.DBConfiguratoreHelper;
import V5.Ingsoft.model.DBDatesHelper;
import V5.Ingsoft.model.DBFruitoreHelper;
import V5.Ingsoft.model.DBIscrizioniHelper;
import V5.Ingsoft.model.DBLuoghiHelper;
import V5.Ingsoft.model.DBTipoVisiteHelper;
import V5.Ingsoft.model.DBVisiteHelper;
import V5.Ingsoft.model.DBVolontarioHelper;

public final class DBHelperFactory implements BaseFactory {

    private static final Map<String, Supplier<? extends Product>> registry = new HashMap<>();

    static {
        registry.put(DBConfiguratoreHelper.CLASSNAME, DBConfiguratoreHelper::new);
        registry.put(DBVolontarioHelper.CLASSNAME, DBVolontarioHelper::new);
        registry.put(DBFruitoreHelper.CLASSNAME, DBFruitoreHelper::new);
        registry.put(DBTipoVisiteHelper.CLASSNAME, DBTipoVisiteHelper::new);
        registry.put(DBVisiteHelper.CLASSNAME, DBVisiteHelper::new);
        registry.put(DBLuoghiHelper.CLASSNAME, DBLuoghiHelper::new);
        registry.put(DBIscrizioniHelper.CLASSNAME, DBIscrizioniHelper::new);
        registry.put(DBDatesHelper.CLASSNAME, DBDatesHelper::new);
    }

    @Override
    public <T extends Product> T factoryMethod(String type, Class<T> clazz) {
        Supplier<? extends Product> supplier = registry.get(type);
        if (supplier == null) {
            throw new UnsupportedOperationException(
                "Factory non implementata per il tipo: " + type
            );
        }
        Product p = supplier.get();
        if (!clazz.isInstance(p)) {
            throw new ClassCastException(
                "Factory ha restituito un tipo diverso da " + clazz.getSimpleName()
            );
        }
        return clazz.cast(p);
    }
}
