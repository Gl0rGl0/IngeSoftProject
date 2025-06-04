package GUI.it.proj;

import GUI.it.proj.utils.interfaces.ListDeleter;
import GUI.it.proj.utils.interfaces.ListEditer;

public class test<T> implements ListEditer<T>, ListDeleter<T>{

    @Override
    public void refreshItems() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshItems'");
    }

    @Override
    public void removeItem(String item) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeItem'");
    }

    @Override
    public void editItem(String item) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'editItem'");
    }
    
}
