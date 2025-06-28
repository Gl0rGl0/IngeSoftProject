package GUI.it.proj.utils;

import GUI.it.proj.frame.FruitoriViewController;
import GUI.it.proj.frame.LuoghiViewController;
import GUI.it.proj.frame.TipoVisiteViewController;
import GUI.it.proj.utils.interfaces.ListBase;
import GUI.it.proj.utils.interfaces.ListDeleter;
import GUI.it.proj.utils.interfaces.ListEditer;
import V5.Ingsoft.controller.item.interfaces.Deletable;
import V5.Ingsoft.controller.item.interfaces.Informable;
import V5.Ingsoft.controller.item.persone.Iscrizione;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.controller.item.real.Visita;
import V5.Ingsoft.controller.item.statuses.StatusItem;
import V5.Ingsoft.controller.item.statuses.Statuses;
import V5.Ingsoft.model.Model;
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
import javafx.scene.control.Tooltip;

public class CellController<T extends Informable> {
    @FXML private HBox container;
    @FXML private VBox textContainer;
    @FXML private Label primaryText;
    @FXML private Label secondaryText;
    @FXML private Label importantDate;
    @FXML private Region spacer;
    @FXML private Button deleteButton;
    @FXML private Button editButton;
    @FXML private ImageView binImage;
    @FXML private ImageView confirmImage;
    
    private T item;
    ListBase<T> parent;
    ListEditer<T> parentEditer;
    ListDeleter<T> parentDeleter;
    
    @FXML
    private void initialize() {
        
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);

        textContainer.prefWidthProperty().unbind();
        textContainer.prefWidthProperty().bind(Bindings.multiply(container.widthProperty(), 0.95));

    }
    
    public void showEditButton() {
        textContainer.prefWidthProperty().bind(Bindings.multiply(container.widthProperty(), 0.55));
        editButton.setVisible(true);
        editButton.setManaged(true);
    }
    
    public void showDeleteButton() {
        textContainer.prefWidthProperty().bind(Bindings.multiply(container.widthProperty(), 0.55));
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);
        deleteButton.setGraphic(binImage);
    }
    
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
                handleDisable(persona.getStatus());
            }
            case Luogo luogo -> {
                primaryText.setText(luogo.getName());
                secondaryText.setText(luogo.getDescription());
                handleDisable(luogo.getStatus());
            }
            case Visita visita -> {
                primaryText.setText(visita.getTitle() + " " + visita.getDate());
                secondaryText.setText(visita.getTipoVisita().getDescription());
            }
            case TipoVisita tvisita -> {
                primaryText.setText(tvisita.getTitle());
                Luogo luogo = Model.getInstance().dbLuoghiHelper.getItem(tvisita.getLuogoUID());
                if(luogo != null)
                    importantDate.setText(luogo.getName());
                secondaryText.setText(tvisita.getDescription());
                handleDisable(tvisita.getStatus());
            }
            case Iscrizione iscrizione -> {
                Visita v = Model.getInstance().dbVisiteHelper.getItem(iscrizione.getVisitaUID());
                if(v == null) return;

                primaryText.setText(v.getTitle());
                secondaryText.setText(v.getDate().toString());

                importantDate.setText(iscrizione.getUID());
            }
            default -> {
            }
        }
    }

    private void handleDisable(Statuses status){
        switch((StatusItem) status){
            case StatusItem.PENDING_REMOVE, StatusItem.DISABLED -> {
                if(disabled)
                    return;
                
                // Solo se è PENDING_REMOVE, aggiungi un tooltip
                if(status == StatusItem.PENDING_REMOVE) {
                    Tooltip tip = new Tooltip("This item will be removed.");
                    Tooltip.install(deleteButton, tip);
                    importantDate.setText("PENDING REMOVE: " + ((Deletable) item).getDeletionDate());
                }

                deleteButton.getStyleClass().remove("red-btn");
                deleteButton.getStyleClass().add("grey-btn");
                deleteButton.setDisable(true);
                disabled = true;
            }
            case StatusItem.ACTIVE -> {
                if(!disabled)
                    return;
                
                    // Rimuovi eventuale tooltip
                deleteButton.setTooltip(null);
                
                deleteButton.getStyleClass().remove("grey-btn");
                deleteButton.getStyleClass().add("red-btn");
                disabled = false;
                deleteButton.setDisable(false);
            }

            case StatusItem.PENDING_ADD -> {
                importantDate.setText("PENDING ADD: " + ((Deletable) item).getUsableDate());
            }
            
            default -> {}
        }
    }
    
    private boolean confirm = false;
    @FXML
    private void handleDelete() {
        if (confirm) {
            this.parentDeleter.removeItem(item.getMainInformation());
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
    private void handleEdit() {
        this.parentEditer.editItem(item.getMainInformation());
        confirm = false;
        deleteButton.setGraphic(binImage);
    }

    public void setParent(ListBase<T> parent) {
        this.parent = parent;
    }

    public void setParentEditer(ListEditer<T> parente) {
        this.parentEditer = parente;
    }

    public void setParentDeleter(ListDeleter<T> parentd) {
        this.parentDeleter = parentd;
    }
}