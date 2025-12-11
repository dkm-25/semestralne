package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;


public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    protected void onLoginClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        errorLabel.setText("");

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Введіть email і пароль");
            return;
        }

        boolean ok = ApiClient.login(email, password);

        if (!ok) {
            errorLabel.setText("Невірний email або пароль");
            return;
        }

        openMainWindow();
    }

    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/main.fxml"));
            Scene scene = new Scene(loader.load());

            MainController controller = loader.getController();
            controller.init(ApiClient.getUserId(), ApiClient.getUserName());

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Collaborative Study Platform");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openRegisterWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/register.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
