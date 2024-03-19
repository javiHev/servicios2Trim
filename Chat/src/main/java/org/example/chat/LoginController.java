package org.example.chat;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.security.PrivateKey;
import java.security.PublicKey;

public class LoginController {

    public static LoginController getController;
    public static PrivateKey privateKey;
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    private PublicKey publicKeyDestinatario;
  /*  public static PrivateKey obtenerPrivateKey() {
        return this.privateKey;
    }*/

    // Este método intenta el inicio de sesión falso y cambia a la pantalla de chat
    @FXML
    private void handleLoginAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (!username.isEmpty() && !password.isEmpty()) {
            // Aquí, supongamos que la autenticación es siempre exitosa
            showChatView();
        } else {
            showAlert("Inicio de Sesión Fallido", "Por favor, ingrese un nombre de usuario y contraseña.");
        }
    }

    // Muestra la vista del chat principal después del inicio de sesión
    private void showChatView() {
        try {
            // Carga el FXML del chat
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/chat/chat_ui.fxml"));
            Parent root = loader.load();
            ClienteChatController controller=loader.getController();
            controller.setPublicKey(this.publicKeyDestinatario);
            controller.setPrivateKey(this.privateKey);
            // Aquí se podría pasar la dirección del servidor y el puerto si son necesarios
            ClienteChatController chatController = loader.getController();
            chatController.connectToServer("localhost", 12345);

            // Muestra la vista del chat
            Stage stage = (Stage) usernameField.getScene().getWindow(); // Obtiene el escenario actual
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo cargar la vista del chat.");
        }
    }

    // Método para mostrar alertas
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }




    public void setPublicKey(PublicKey publicKey) {
        this.publicKeyDestinatario = publicKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public void initialize(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKeyDestinatario = publicKey;
        this.privateKey = privateKey;
    }
}
