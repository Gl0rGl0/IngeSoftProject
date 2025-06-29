package V5.Ingsoft.factory;

import V5.Ingsoft.controller.item.interfaces.DBHelperInterface;
import V5.Ingsoft.model.*;
import V5.Ingsoft.model.helper.DBConfiguratoreHelper;
import V5.Ingsoft.model.helper.DBDatesHelper;
import V5.Ingsoft.model.helper.DBFruitoreHelper;
import V5.Ingsoft.model.helper.DBIscrizioniHelper;
import V5.Ingsoft.model.helper.DBLuoghiHelper;
import V5.Ingsoft.model.helper.DBTipoVisiteHelper;
import V5.Ingsoft.model.helper.DBVisiteHelper;
import V5.Ingsoft.model.helper.DBVolontarioHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class DBHelperFactory implements BaseFactory {

    private static final Map<Class<? extends DBHelperInterface<?>>, Supplier<? extends DBHelperInterface<?>>> registry = new HashMap<>();

    static {
        registry.put(DBConfiguratoreHelper.class, DBConfiguratoreHelper::new);
        registry.put(DBVolontarioHelper.class, DBVolontarioHelper::new);
        registry.put(DBFruitoreHelper.class, DBFruitoreHelper::new);
        registry.put(DBTipoVisiteHelper.class, DBTipoVisiteHelper::new);
        registry.put(DBVisiteHelper.class, DBVisiteHelper::new);
        registry.put(DBLuoghiHelper.class, DBLuoghiHelper::new);
        registry.put(DBIscrizioniHelper.class, DBIscrizioniHelper::new);
        registry.put(DBDatesHelper.class, DBDatesHelper::new);
    }

    @Override
    public <T extends DBHelperInterface<?>> T factoryMethod(Class<T> clazz) {
        Supplier<? extends DBHelperInterface<?>> supplier = registry.get(clazz);
        if (supplier == null) {
            throw new UnsupportedOperationException(
                    "Factory non implementata per il tipo: " + clazz.getSimpleName());
        }
        DBHelperInterface<?> p = supplier.get();
        if (!clazz.isInstance(p)) {
            throw new ClassCastException(
                    "Factory ha restituito un tipo diverso da " + clazz.getSimpleName() + "( " + p.getClass().getSimpleName() + " )"
            );
        }
        return clazz.cast(p);
    }
}
