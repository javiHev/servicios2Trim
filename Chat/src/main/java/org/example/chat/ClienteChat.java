package org.example.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ClienteChat extends Application {

    private Socket clientSocket;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Override
    public void start(Stage primaryStage) {
        try {
            generateKeyPair();

            String serverAddress = "localhost";
            int port = 12345;
            clientSocket = new Socket(serverAddress, port);

            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStream.writeObject(publicKey);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/chat/login.fxml"));
            Parent root = fxmlLoader.load();

            LoginController loginController = fxmlLoader.getController();
            loginController.initialize(publicKey, privateKey);

            Scene scene = new Scene(root);
            primaryStage.setTitle("Inicio de Sesión - Chat");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo iniciar la aplicación: " + e.getMessage());
        }
    }

    private void generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
