package GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login Example");

        // Creazione di un GridPane con padding e gap
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(25));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.getStyleClass().add("root");

        // Label e TextField per Username
        Label userLabel = new Label("Username:");
        grid.add(userLabel, 0, 0);

        TextField userTextField = new TextField();
        userTextField.getStyleClass().add("text-field");
        grid.add(userTextField, 1, 0);

        // Label e PasswordField per Password
        Label pwLabel = new Label("Password:");
        grid.add(pwLabel, 0, 1);

        PasswordField pwField = new PasswordField();
        pwField.getStyleClass().add("text-field");
        grid.add(pwField, 1, 1);

        // Bottone per il login
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");
        grid.add(loginButton, 1, 2);

        // Aggiunta dell'evento per il bottone
        loginButton.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwField.getText();
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
        });

        // Creazione della scena con il file CSS applicato
        Scene scene = new Scene(grid, 300, 200);
        scene.getStylesheets().add(getClass().getResource("/GUI/resources/login.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
