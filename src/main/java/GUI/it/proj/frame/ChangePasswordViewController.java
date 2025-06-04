package GUI.it.proj.frame;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import GUI.it.proj.Launcher;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;

public class ChangePasswordViewController {
    public final static String ID = "changepsw";

    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button resetButton;

    private GenericFrameController parent;

    public void setParentController(GenericFrameController p){
        this.parent = p;
    }

    @FXML
    public void initialize(){
        this.passwordField.clear();
        this.confirmPasswordField.clear();
    }

    @FXML
    public void handleResetAction() {
        String newPass = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (newPass.length() < 6) {
            Launcher.toast(Payload.error("The password lenght must be at least 6 char.", confirmPass));
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Launcher.toast(Payload.error("The passwords must be equals.", confirmPass));
            return;
        }
        
        Payload<?> res = Launcher.controller.interpreter("changepsw " + newPass);
        
        Launcher.toast(res);

        if(res != null && res.getStatus() == Status.INFO)
            parent.showHome();
    }
}