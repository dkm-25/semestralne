package client;

import client.dto.User;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProfileController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private long userId;

    public void init(User user) {
        this.userId = user.getId();

        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
    }

    @FXML
    private void onSave() {
        try {
            String newName = nameField.getText();
            String newEmail = emailField.getText();
            String newPassword = passwordField.getText().isBlank() ? null : passwordField.getText();

            ApiClient.updateUser(userId, newName, newEmail, newPassword);

            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClose() {
        close();
    }

    private void close() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
