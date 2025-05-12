package GUI.it.proj.utils;

import GUI.it.proj.frame.FruitoriViewController;
import GUI.it.proj.frame.LuoghiViewController;
import GUI.it.proj.frame.TipoVisiteViewController;
import GUI.it.proj.utils.interfaces.ListBase;
import GUI.it.proj.utils.interfaces.ListEditer;
import V5.Ingsoft.controller.item.luoghi.Luogo;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import V5.Ingsoft.controller.item.luoghi.Visita;
import V5.Ingsoft.controller.item.persone.Persona;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CellController<T> {
    @FXML
    private HBox container;
    @FXML
    private VBox textContainer;
    @FXML
    private Label usernameText;
    @FXML
    private Label descriptionText;
    @FXML
    private Region spacer;
    @FXML
    private Button deleteButton;
    @FXML
    private ImageView binImage;
    @FXML
    private ImageView confirmImage;

    private T item;
    ListBase<T> parentView;
    ListEditer<T> parentEditer;

    public void setType(String type) {
        switch (type) {
            case   TipoVisiteViewController.ID, 
                   LuoghiViewController.ID, 
                   FruitoriViewController.ID -> {
                descriptionText.setManaged(true);
                descriptionText.setVisible(true);
            }
            default -> {}
        }
    }

    public void setItem(T item) {
        this.item = item;

        switch (item) {
            case Persona persona -> {
                usernameText.setText(persona.getUsername());
                if(!persona.isUsable())
                    deleteButton.setDisable(true);
            }
            case Luogo luogo -> {
                usernameText.setText(luogo.getName());
                descriptionText.setText(luogo.getDescription());
                if(!luogo.isUsable())
                    deleteButton.setDisable(true);
            }
            case Visita visita -> {
                usernameText.setText(visita.getTitle());
                descriptionText.setText(visita.getTipoVisita().getDescription());
            }
            case TipoVisita tvisita -> {
                usernameText.setText(tvisita.getTitle());
                descriptionText.setText(tvisita.getDescription());
                if(!tvisita.isUsable())
                    deleteButton.setDisable(true);
            }
            default -> {
            }
        }
    }

    public void setViewController(ListBase<T> p) {
        if(p instanceof ListEditer){
            this.parentEditer = (ListEditer<T>) p;
        }
        else{
            this.parentView = p;
            hideAllButton();
        }
    }

    private boolean confirm = false;

    @FXML
    private void handleDelete() {
        if (confirm) {
            this.parentEditer.removeItem(item);
            confirm = false;
            deleteButton.setGraphic(binImage);
        } else {
            confirm = true;
            deleteButton.setGraphic(confirmImage);
        }
    }

    @FXML
    private void initialize() {
        deleteButton.setGraphic(binImage);

        textContainer.prefWidthProperty().bind(Bindings.multiply(container.widthProperty(), 0.55));
    }

    public void hideAllButton() {
        deleteButton.setManaged(false);
        deleteButton.setVisible(false);
        textContainer.prefWidthProperty().unbind();
        textContainer.prefWidthProperty().bind(Bindings.multiply(container.widthProperty(), 0.95));
    }
}