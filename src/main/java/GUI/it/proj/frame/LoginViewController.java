package GUI.it.proj.frame;

import java.net.URL;
import java.util.ResourceBundle;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import com.dlsc.gemsfx.EnhancedPasswordField;

import GUI.it.proj.Launcher;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;

public class LoginViewController implements Initializable {
    public final static String ID = "login";

    @FXML private TextField userTextField;
    @FXML private EnhancedPasswordField pwField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;

    /**
     * Metodo chiamato automaticamente dopo l'iniezione degli elementi FXML.
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) { 
   
        //set right node
        FontIcon fontIcon = new FontIcon();
        fontIcon.iconCodeProperty().bind(pwField.showPasswordProperty().map(it -> it ? MaterialDesign.MDI_EYE : MaterialDesign.MDI_EYE_OFF));

        StackPane right = new StackPane(fontIcon);
        right.getStyleClass().add("right-icon-wrapper");
        right.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                pwField.setShowPassword(!pwField.isShowPassword());
            }
        });

        pwField.setRight(right);
    }

    /**
     * Gestisce il click sul bottone di login.
     */
    @FXML
    private void handleLoginAction() {
        if(!Launcher.getInstance().controller.setupCompleted()){
            messageLabel.setText("Se sei un Configuratore, si prega di completare la fase di SETUP da terminale prima di accedere all'applicazione.");
            messageLabel.setVisible(true);
            return;
        }
        
        String username = userTextField.getText();
        String password = pwField.getText();

        if(username == null || username.isEmpty()){
            userTextField.getStyleClass().add("error-border");
            return;
        }else{
            userTextField.getStyleClass().remove("error-border");
        }

        if(password == null || password.isEmpty()){
            pwField.getStyleClass().add("error-border");
            return;
        }else{
            pwField.getStyleClass().remove("error-border");
        }

        Payload<?> res = Launcher.getInstance().controller.interpreter(String.format("login %s %s", username, password));
        if (res != null && res.getStatus() != Status.ERROR) {
            userTextField.clear();
            pwField.clear();
            Launcher.getInstance().setRoot(GenericFrameController.ID);
        } else {
            messageLabel.setVisible(true);
        }
    }
}