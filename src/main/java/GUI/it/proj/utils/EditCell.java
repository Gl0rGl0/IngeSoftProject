package GUI.it.proj.utils;

import GUI.it.proj.utils.interfaces.ListBase;
import V5.Ingsoft.controller.item.Informable;

public class EditCell<T extends Informable> extends Cell<T>{

    public EditCell(ListBase<T> parent, String type, boolean buttonVisible) {
        super(parent, type);
    }
    
}
