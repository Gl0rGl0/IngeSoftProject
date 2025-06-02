package V5.Ingsoft.controller.item.interfaces;

import V5.Ingsoft.util.Date;

import java.util.List;

public interface DBWithStatus {
    List<? extends Deletable> getItems();

    default void checkItems(Date d) {
        getItems().forEach(i -> i.checkStatus(d));
    }
}
