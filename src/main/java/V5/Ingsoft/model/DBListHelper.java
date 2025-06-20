package V5.Ingsoft.model;

import V5.Ingsoft.controller.item.interfaces.DBHelperInterface;
import V5.Ingsoft.controller.item.interfaces.StorageManager;
import V5.Ingsoft.controller.item.interfaces.Storageble;
import V5.Ingsoft.util.JsonStorage;

import java.util.ArrayList;
import java.util.List;

public abstract class DBListHelper<T extends Storageble> implements DBHelperInterface<T> {
    protected final ArrayList<T> cachedItems = new ArrayList<>();
    private StorageManager<T> sm;

    private StorageManager<T> createSM(String path, Class<T> claz){
        return new JsonStorage<>(path, claz);
    }

    public DBListHelper(String path, Class<T> claz) {
        this.sm = createSM(path, claz);
        loadDBJson();
    }

    public DBListHelper(String path, Class<T> claz, ArrayList<T> list) {
        this.sm = createSM(path, claz);
        list.forEach(i -> cachedItems.add(i));
    }

    public void loadDBJson() {
        getDBfromFile().forEach(i -> cachedItems.add(i));
    }

    public List<T> getDBfromFile() {
        return sm.loadList();
    }

    public List<T> getItems() {
        return new ArrayList<>(cachedItems);
    }

    public boolean addItem(T item) {
        if(cachedItems.contains(item))
            return false;

        cachedItems.add(item);
        return saveDB();
    }

    public boolean removeItem(T item) {
        if (cachedItems.remove(item))
            return saveDB();
        return false;
    }

    public boolean saveDB() {
        return sm.saveList(getItems());
    }

    public void clear() {
        sm.clearList();
    }

    public String getClassName() {
        return this.getClass().getSimpleName();
    }
}
