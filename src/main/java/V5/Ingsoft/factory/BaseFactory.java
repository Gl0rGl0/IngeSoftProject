package V5.Ingsoft.factory;

import V5.Ingsoft.controller.item.interfaces.Product;

public interface BaseFactory {
    <T extends Product> T factoryMethod(Class<T> clazz);
}
