package V1.Ingsoft.model;

import java.util.ArrayList;
import java.util.Collection;

import V1.Ingsoft.util.JsonStorage;

public abstract class DBAbstractHelper<T> {
    private final String fileJson;
    protected final Class<T> clazz;

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

    public abstract void close();
}