package GUI.it.proj.frame;

public interface ListViewController<T> {
    void addItem(T item);

    void removeItem(T item);

    void modifyItem(T item);
}
