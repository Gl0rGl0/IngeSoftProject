package V5.Ingsoft.controller.item.interfaces;

import java.util.List;

public interface DBHelperInterface<T extends Storageble> {
    void loadDBJson();

    List<T> getDBfromFile();

    List<T> getItems();

    boolean addItem(T item);

    boolean removeItem(T item);

    boolean saveDB();

    void clear();

    String getClassName();
}
