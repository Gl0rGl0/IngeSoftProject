package V5.Ingsoft.factory;

import V5.Ingsoft.controller.item.interfaces.DBHelperInterface;

public interface BaseFactory {
    <T extends DBHelperInterface<?>> T factoryMethod(Class<T> clazz);
}
