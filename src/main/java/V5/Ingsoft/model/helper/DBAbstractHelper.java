package V5.Ingsoft.model.helper;

import java.util.ArrayList;

import V5.Ingsoft.controller.item.interfaces.Storageble;

public abstract class DBAbstractHelper<T extends Storageble> extends DBMapHelper<T> {
    public DBAbstractHelper(String path, Class<T> claz) {
        super(path, claz);
    }

    public DBAbstractHelper(String path, Class<T> claz, ArrayList<T> list) {
        super(path, claz, list);
    }
}