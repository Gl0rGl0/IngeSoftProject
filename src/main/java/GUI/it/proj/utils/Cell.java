package GUI.it.proj.utils;

import GUI.it.proj.utils.interfaces.ListBase;
import GUI.it.proj.utils.interfaces.ListEditer;
import V5.Ingsoft.controller.item.luoghi.Visita;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;

public class Cell<T> extends ListCell<T> {
    private Parent root;
    private CellController<T> controller;

    //al massimo parent implementerà anche l'editer
    public Cell(ListEditer<T> parent, String type, boolean buttonVisible) {
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
            controller.setViewController(parent);
            controller.setType(type);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load FXML: " + resourcePath, e);
        }

        this.setOnMouseClicked(e -> {
            T item = getItem();
            if (!isEmpty() && item instanceof Visita) {
                System.out.println("Cella cliccata all'indice: " + getIndex());
                System.out.println("Elemento associato: " + item);
            }
        });
    }

    public Cell(ListBase<T> parent, String type, boolean buttonVisible) {
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
            controller.setViewController(parent);
            controller.setType(type);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load FXML: " + resourcePath, e);
        }

        if (!buttonVisible) {
            controller.hideAllButton();
        }

        this.setOnMouseClicked(e -> {
            T item = getItem();
            if (!isEmpty() && item instanceof Visita) {
                System.out.println("Cella cliccata all'indice: " + getIndex());
                System.out.println("Elemento associato: " + item);
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
