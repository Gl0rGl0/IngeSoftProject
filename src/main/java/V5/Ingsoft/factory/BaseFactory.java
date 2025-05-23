package V5.Ingsoft.factory;

import V5.Ingsoft.controller.item.interfaces.Product;

public interface BaseFactory {
    <T extends Product> T factoryMethod(String type, Class<T> clazz);
}
