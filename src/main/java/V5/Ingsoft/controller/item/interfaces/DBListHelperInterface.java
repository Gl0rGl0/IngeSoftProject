package V5.Ingsoft.controller.item.interfaces;

public interface DBListHelperInterface<T extends Storageble> extends DBHelperInterface<T> {
    boolean removeItem(T item);
}
