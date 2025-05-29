package V5.Ingsoft.model;

import V5.Ingsoft.util.JsonStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public abstract class DBAbstractHelper<T> {
    protected final Class<T> clazz;
    protected final HashMap<String, T> cachedItems = new HashMap<>();
    private final String fileJson;

    public DBAbstractHelper(String path, Class<T> claz) {
        this.fileJson = path;
        this.clazz = claz;
    }

    public ArrayList<T> getJson() {
        return JsonStorage.loadList(fileJson, clazz);
    }

    public boolean saveJson(Collection<T> toSave) {
        return JsonStorage.saveList(fileJson, toSave);
    }

    public boolean saveJson() {
        return JsonStorage.saveList(fileJson, getItems());
    }

    //Creata una copia
    protected ArrayList<T> getItems() {
        return new ArrayList<>(cachedItems.values());
    }

    public abstract void close();

    public void clear() {
        JsonStorage.clearList(fileJson);
    }

    public String getClassName() {
        return this.getClass().getSimpleName();
    }
}