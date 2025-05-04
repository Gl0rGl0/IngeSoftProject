package GUI.it.proj.utils;

import java.net.URL;
import java.util.ResourceBundle;

import GUI.it.proj.frame.ListViewController;
import GUI.it.proj.frame.LuoghiViewController;
import GUI.it.proj.frame.VisiteViewController;
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
    ListViewController<T> parent;

    public void setType(String type) {
        switch (type) {
            case "CONFIGURATORE", "FRUITORE" -> {
                descriptionText.setManaged(false);
                descriptionText.setVisible(false);
                modifyButton.setVisible(false);
                modifyButton.setManaged(false);
            }
            case "VOLONTARIO" -> {
                descriptionText.setManaged(false);
                descriptionText.setVisible(false);
            }
            case VisiteViewController.ID -> {
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
                usernameText.setText(luogo.getTitolo());
                descriptionText.setText(luogo.descrizione);
            }
            case Visita visita -> {
                usernameText.setText(visita.getTitolo());
                descriptionText.setText(visita.descrizione);
            }
            default -> {
            }
        }

    }

    public void setViewController(ListViewController<T> p) {
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
        parent.modifyItem(item);
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