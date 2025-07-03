package V5.Ingsoft.controller.item.interfaces;

import java.util.List;

import java.util.Collection;

public interface StorageManager<T> {

    // Metodi per liste
    List<T> loadList();
    boolean saveList(Collection<T> collection);
    void clearList();

    // Metodi per oggetti singoli
    T loadObject();
    boolean saveObject(T object);
}