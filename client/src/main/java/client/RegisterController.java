package client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmField;

    @FXML
    private Label infoLabel;

    @FXML
    private void onRegisterClick() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passwordField.getText().trim();
        String confirm = confirmField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            infoLabel.setText("Всі поля мають бути заповнені");
            return;
        }

        if (!pass.equals(confirm)) {
            infoLabel.setText("Паролі не співпадають");
            return;
        }

        boolean ok = ApiClient.register(name, email, pass);

        if (ok) {
            infoLabel.setStyle("-fx-text-fill: green;");
            infoLabel.setText("Успішна реєстрація!");

            ((Stage) nameField.getScene().getWindow()).close();
        } else {
            infoLabel.setStyle("-fx-text-fill: red;");
            infoLabel.setText("Помилка реєстрації");
        }
    }
}
