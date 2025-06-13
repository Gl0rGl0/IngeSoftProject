package GUI.it.proj.utils;

import GUI.it.proj.frame.HomeVisiteViewController;
import GUI.it.proj.utils.interfaces.ListBase;
import GUI.it.proj.utils.interfaces.ListDeleter;
import GUI.it.proj.utils.interfaces.ListEditer;
import V5.Ingsoft.controller.item.interfaces.Informable;
import V5.Ingsoft.controller.item.real.Visita;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;

public class Cell<T extends Informable> extends ListCell<T> {
    private Parent root;
    private CellController<T> controller;

    public Cell(ListBase<T> parent, String type) {
        // Load FXML directly to access the controller instance
        String resourcePath = "/GUI/frame/cell-view.fxml";
        URL fxmlUrl = getClass().getResource(resourcePath);
        if (fxmlUrl == null) {
            throw new IllegalArgumentException("FXML resource not found: " + resourcePath);
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        try {
            root = loader.load();
            controller = loader.getController(); // Get controller directly from loader
            if (controller == null) {
                throw new IllegalStateException("Controller not set in FXML file: " + resourcePath);
            }
            controller.setParent(parent);
            controller.setType(type);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load FXML: " + resourcePath, e);
        }

        boolean editer = parent instanceof ListEditer;
        boolean deleter = parent instanceof ListDeleter;

        if(editer){
            controller.setParentEditer((ListEditer<T>) parent);
            controller.showEditButton();
        }

        if(deleter){
            controller.setParentDeleter((ListDeleter<T>) parent);
            controller.showDeleteButton();
        }

        this.setOnMouseClicked(e -> {
            T item = getItem();
            if (!isEmpty() && item instanceof Visita) {
                // System.out.println("Cella cliccata all'indice: " + getIndex());
                // System.out.println("Elemento associato: " + item);
                ((HomeVisiteViewController) parent).showVisita((Visita) item);
            }
        });
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            controller.setItem(item);
            setGraphic(root);
        }
    }
}
