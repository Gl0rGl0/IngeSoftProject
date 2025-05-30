package V5.Ingsoft.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import V5.Ingsoft.controller.item.interfaces.DBMapHelperInterface;
import V5.Ingsoft.controller.item.interfaces.Storageble;
import V5.Ingsoft.util.JsonStorage;

public abstract class DBMapHelper<T extends Storageble> implements DBMapHelperInterface<T>{    
    protected final Class<T> clazz;
    protected final HashMap<String, T> cachedItems = new HashMap<>();
    private final String fileJson;

    public DBMapHelper(String path, Class<T> claz) {
        this.fileJson = path;
        this.clazz = claz;

        loadDB();
    }

    public void loadDB(){
        getDBfromFile().forEach(i -> cachedItems.put(i.getUID(), i));
    }

    public List<T> getDBfromFile() {
        return JsonStorage.loadList(fileJson, clazz);
    }

    public List<T> getItems() {
        return new ArrayList<>(cachedItems.values());
    }

    public T getItem(String uid){
        return cachedItems.get(uid);
    }

    public boolean addItem(T item){
        if(cachedItems.put(item.getUID(), item) == null)
            return JsonStorage.saveList(fileJson, getItems());
        return false;
    }

    public boolean removeItem(String id){
        if(cachedItems.remove(id) != null)
            return JsonStorage.saveList(fileJson, getItems());
        return false;
    }

    public boolean saveDB() {
        return JsonStorage.saveList(fileJson, getItems());
    }

    public void clear() {
        JsonStorage.clearList(fileJson);
    }

    public String getClassName() {
        return this.getClass().getSimpleName();
    }
}
