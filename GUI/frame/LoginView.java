package GUI.frame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {

    private MainFrame mainFrame;
    private StackPane root;

    public LoginView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        createView();
    }

    private void createView() {
        // GridPane per il form di login
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(25));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.getStyleClass().add("login-grid");
        grid.setMinSize(300, 200);
        grid.setAlignment(Pos.CENTER);

        TextField userTextField = new TextField();
        userTextField.setPromptText("Username");
        grid.add(userTextField, 1, 0);

        PasswordField pwField = new PasswordField();
        pwField.setPromptText("Password");
        grid.add(pwField, 1, 1);

        Label messageLabel = new Label();
        grid.add(messageLabel, 1, 3);
        messageLabel.getStyleClass().addAll("message-label", "error-label");
        messageLabel.setVisible(false);

        // Bottone Login
        Button loginButton = new Button("Login");
        grid.add(loginButton, 1, 2);
        javafx.application.Platform.runLater(() -> loginButton.requestFocus());

        loginButton.setOnAction(_ -> {
            String username = userTextField.getText();
            String password = pwField.getText();
            System.out.println(username + " " + password);
            if (username.length() > 0 && password.length() > 0) {
                if ("admin".equals(username) && "admin".equals(password)) { // QUA LOGICA DEL LOGIN...
                    mainFrame.showMainApp();
                } else {
                    messageLabel.setVisible(true);
                    messageLabel.setText("Password o username errati");
                }
            }
        });

        // **Titolo "Visite"**
        Label titleLabel = new Label("Visite");
        titleLabel.getStyleClass().add("title-label"); // Stile CSS
        titleLabel.setAlignment(Pos.CENTER);

        // **VBox per centrare titolo e form**
        VBox vbox = new VBox(20, titleLabel, grid); // Spaziatura tra titolo e form
        vbox.setAlignment(Pos.CENTER);

        // StackPane per centrare tutto
        root = new StackPane(vbox);
        StackPane.setAlignment(vbox, Pos.CENTER);
    }

    public StackPane getView() {
        return root;
    }

}