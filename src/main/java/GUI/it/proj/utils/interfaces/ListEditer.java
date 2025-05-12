package GUI.it.proj.utils.interfaces;

public interface ListEditer<T> extends ListBase<T> {
    void addItem(T item);

    void removeItem(T item);
}
