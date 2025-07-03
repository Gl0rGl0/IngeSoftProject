package V5.Ingsoft.controller.item.interfaces;

public interface DBMapHelperInterface<T extends Storageble> extends DBHelperInterface<T> {
    boolean removeItem(String uid);

    T getItem(String id);
}
