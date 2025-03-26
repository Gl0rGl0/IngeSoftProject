package V4.Ingsoft.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import V4.Ingsoft.util.JsonStorage;

public abstract class DBAbstractHelper<T> {
    private final String fileJson;
    protected final Class<T> clazz;

    protected final HashMap<String, T> cachedItems = new HashMap<>();

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

    protected ArrayList<T> getItems() {
        return new ArrayList<>(cachedItems.values());
    }

    public abstract void close();

    public boolean clear() {
        return JsonStorage.clearList(fileJson);
    }
}