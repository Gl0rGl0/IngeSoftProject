package V5.Ingsoft.controller.item.interfaces;

import java.util.List;

import V5.Ingsoft.util.Date;

public interface DBWithStatus {
    List<? extends Deletable> getItems();
    
    default void checkItems(Date d){
        getItems().forEach(i -> i.checkStatus(d));
    }
}
