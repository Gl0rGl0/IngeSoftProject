package V5.Ingsoft.model.helper;

import V5.Ingsoft.controller.item.interfaces.DBMapHelperInterface;
import V5.Ingsoft.controller.item.interfaces.StorageManager;
import V5.Ingsoft.controller.item.interfaces.Storageble;
import V5.Ingsoft.util.JsonStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class DBMapHelper<T extends Storageble> implements DBMapHelperInterface<T> {
    protected final HashMap<String, T> cachedItems = new HashMap<>();
    private StorageManager<T> sm;

    private StorageManager<T> createSM(String path, Class<T> claz){
        return new JsonStorage<>(path, claz);
    }

    public DBMapHelper(String path, Class<T> claz) {
        this.sm = createSM(path, claz);
        loadDBJson();
    }

    public DBMapHelper(String path, Class<T> claz, ArrayList<T> list) {
        this.sm = createSM(path, claz);
        list.forEach(i -> cachedItems.put(i.getUID(), i));
    }

    public void loadDBJson() {
        getDBfromFile().forEach(i -> cachedItems.put(i.getUID(), i));
    }

    public List<T> getDBfromFile() {
        return sm.loadList();
    }

    public List<T> getItems() {
        return new ArrayList<>(cachedItems.values());
    }

    public T getItem(String uid) {
        return cachedItems.get(uid);
    }

    public boolean addItem(T item) {
        if(cachedItems.containsKey(item.getUID()))
            return false;

        cachedItems.put(item.getUID(), item);
        return saveDB();
    }

    public boolean removeItem(String id) {
        if (cachedItems.remove(id) != null)
            return saveDB();
        return false;
    }

    public boolean removeItem(T item) {
        return removeItem(item.getUID());
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
