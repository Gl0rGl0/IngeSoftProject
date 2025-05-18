package GUI.it.proj.utils;

import GUI.it.proj.frame.FruitoriViewController;
import GUI.it.proj.frame.LuoghiViewController;
import GUI.it.proj.frame.TipoVisiteViewController;
import GUI.it.proj.utils.interfaces.ListBase;
import GUI.it.proj.utils.interfaces.ListEditer;
import V5.Ingsoft.controller.item.Informable;
import V5.Ingsoft.controller.item.StatusItem;
import V5.Ingsoft.controller.item.luoghi.Luogo;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import V5.Ingsoft.controller.item.luoghi.Visita;
import V5.Ingsoft.controller.item.persone.Persona;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class CellController<T extends Informable> {
    @FXML
    private HBox container;
    @FXML
    private VBox textContainer;
    @FXML
    private Label primaryText;
    @FXML
    private Label secondaryText;
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
                secondaryText.setManaged(true);
                secondaryText.setVisible(true);
            }
            default -> {}
        }
    }

    private boolean disabled = false;
    public void setItem(T item) {
        this.item = item;

        switch (item) {
            case Persona persona -> {
                primaryText.setText(persona.getUsername());
                handleDisable(persona.getStatus() == StatusItem.PENDING_REMOVE);
            }
            case Luogo luogo -> {
                primaryText.setText(luogo.getName());
                secondaryText.setText(luogo.getDescription());
                handleDisable(luogo.getStatus() == StatusItem.PENDING_REMOVE);
            }
            case Visita visita -> {
                primaryText.setText(visita.getTitle());
                secondaryText.setText(visita.getTipoVisita().getDescription());
            }
            case TipoVisita tvisita -> {
                primaryText.setText(tvisita.getTitle());
                secondaryText.setText(tvisita.getDescription());
                handleDisable(tvisita.getStatus() == StatusItem.PENDING_REMOVE);
            }
            default -> {
            }
        }
    }

    private void handleDisable(boolean exec){
        if(exec){
            deleteButton.getStyleClass().remove("red-bnt");
            deleteButton.getStyleClass().add("grey-bnt");
            disabled = true;
            deleteButton.setDisable(true);
        }else if(disabled){
            deleteButton.getStyleClass().remove("grey-bnt");
            deleteButton.getStyleClass().add("red-bnt");
            disabled = false;
            deleteButton.setDisable(false);
        }
    }

    public void setViewController(ListBase<T> p) {
        if(p instanceof ListEditer){
            this.parentEditer = (ListEditer<T>) p;
        } else {
            this.parentView = p;
            hideAllButton();
        }
    }

    private boolean confirm = false;

    @FXML
    private void handleDelete() {
        if (confirm) {
            this.parentEditer.removeItem(item.getMainInformation());
            confirm = false;
            deleteButton.setGraphic(binImage);
        } else {
            confirm = true;
            deleteButton.setGraphic(confirmImage);

            PauseTransition resetConfirm = new PauseTransition(Duration.seconds(5));
            resetConfirm.setOnFinished(e -> {
                if (confirm) {
                    confirm = false;
                    deleteButton.setGraphic(binImage);
                }
            });
            resetConfirm.play();
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