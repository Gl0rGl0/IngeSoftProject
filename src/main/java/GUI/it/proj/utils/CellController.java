package GUI.it.proj.utils;

import java.net.URL;
import java.util.ResourceBundle;

import GUI.it.proj.frame.LuoghiViewController;
import GUI.it.proj.frame.TipoVisiteViewController;
import GUI.it.proj.utils.interfaces.ListBase;
import GUI.it.proj.utils.interfaces.ListEditer;
import V5.Ingsoft.controller.item.luoghi.Luogo;
import V5.Ingsoft.controller.item.luoghi.Visita;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CellController<T> implements Initializable {
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
    private Button modifyButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ImageView binImage;
    @FXML
    private ImageView confirmImage;

    private T item;
    ListBase<T> parent;

    public void setType(String type) {
        switch (type) {
            case "configuratore", "volontario" -> {
                descriptionText.setManaged(false);
                descriptionText.setVisible(false);
                modifyButton.setVisible(false);
                modifyButton.setManaged(false);
            }
            case "fruitore" -> {
                descriptionText.setManaged(false);
                descriptionText.setVisible(false);
            }
            case TipoVisiteViewController.ID -> {
                descriptionText.setManaged(true);
                descriptionText.setVisible(true);
            }
            case LuoghiViewController.ID -> {
                descriptionText.setManaged(true);
                descriptionText.setVisible(true);
            }
            default -> {
            }
        }
    }

    public void setItem(T item) {
        this.item = item;

        switch (item) {
            case Persona persona -> {
                usernameText.setText(persona.getUsername());
            }
            case Luogo luogo -> {
                usernameText.setText(luogo.getName());
                descriptionText.setText(luogo.getDescription());
            }
            case Visita visita -> {
                usernameText.setText(visita.getTitle());
                descriptionText.setText(visita.getTipoVisita().getDescription());
            }
            default -> {
            }
        }

    }

    public void setViewController(ListBase<T> p) {
        this.parent = p;
    }

    private boolean confirm = false;

    @FXML
    private void handleDelete() {
        if (confirm) {
            this.parent.removeItem(item);
            confirm = false;
            deleteButton.setGraphic(binImage);
        } else {
            confirm = true;
            deleteButton.setGraphic(confirmImage);
        }
    }

    @FXML
    private void handleModify() {
        ((ListEditer<T>) parent).modifyItem(item, null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        deleteButton.setGraphic(binImage);

        double d = deleteButton.getWidth();
        double m = modifyButton.getWidth();
        textContainer.prefWidthProperty().bind(Bindings.multiply(container.widthProperty().subtract(d + m), 0.55));
    }

    public void hideAllButton() {
        deleteButton.setManaged(false);
        modifyButton.setManaged(false);
        deleteButton.setVisible(false);
        modifyButton.setVisible(false);
        textContainer.prefWidthProperty().unbind();
        textContainer.prefWidthProperty().bind(Bindings.multiply(container.widthProperty(), 0.95));
    }
}