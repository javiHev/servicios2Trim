package org.example.chat;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import static java.lang.System.out;


public class ClienteChatController {

    @FXML
    private TextArea messageArea;

    @FXML
    private TextField inputBox;
    private Socket socket;
    private PrintWriter out;

    private PublicKey publicKeyDestinatario;
    private PrivateKey privateKey;
    private static final String RSA = "RSA";

    public void setPublicKey(PublicKey publicKey) {
        this.publicKeyDestinatario = publicKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    // Supongamos que ya tienes los métodos para establecer las claves, por simplificación
    public void connectToServer(String serverAddress, int port) {
        try {
            this.socket = new Socket(serverAddress, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);

            // Iniciar un nuevo hilo para escuchar mensajes del servidor
            Thread listenThread = new Thread(this::listenForMessages);
            listenThread.setDaemon(true);
            listenThread.start();

        } catch (IOException e) {
            e.printStackTrace();
            messageArea.appendText("Error al conectarse al servidor: " + e.getMessage() + "\n");
        }
    }

    private void listenForMessages() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String encodedMessage;
            while ((encodedMessage = reader.readLine()) != null) {
                // Desencriptar el mensaje recibido
                byte[] encryptedMessageBytes = Base64.getDecoder().decode(encodedMessage);
                String decryptedMessage = decryptMessage(encryptedMessageBytes, privateKey);

                // Mostrar el mensaje desencriptado en el área de mensajes
                final String messageToShow = decryptedMessage;
                javafx.application.Platform.runLater(() -> messageArea.appendText("Ellos: " + messageToShow + "\n"));
            }
        } catch (IOException e) {
            javafx.application.Platform.runLater(() -> messageArea.appendText("Error al escuchar el mensaje del servidor: " + e.getMessage() + "\n"));
        } catch (Exception e) {
            javafx.application.Platform.runLater(() -> messageArea.appendText("Error al desencriptar el mensaje: " + e.getMessage() + "\n"));
        }
    }


    @FXML
    private void handleSendMessage() {
        try {
            String message = inputBox.getText().trim();

            if (!message.isEmpty()) {
                // Encriptar el mensaje
                byte[] encryptedMessage = encryptMessage(message, publicKeyDestinatario);
                // Convertir el mensaje encriptado a base64 para enviarlo como texto
                String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessage);

                // Enviar el mensaje encriptado y codificado al servidor
                if (out != null) {
                    out.println(encodedMessage);
                    messageArea.appendText("Yo: " + message + "\n");
                } else {
                    messageArea.appendText("No conectado al servidor.\n");
                }

                inputBox.clear(); // Limpiar el inputBox después de enviar
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageArea.appendText("Error al enviar el mensaje: " + e.getMessage() + "\n");
        }
    }

    public byte[] encryptMessage(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(message.getBytes());
    }

    public String decryptMessage(byte[] encryptedMessage, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedMessage);
        return new String(decryptedBytes);
    }

    // Método para recibir y desplegar mensajes (debes llamarlo cuando recibes un mensaje)
    public void onMessageReceived(String encodedMessage) {
        try {
            byte[] encryptedMessageBytes = Base64.getDecoder().decode(encodedMessage);
            String decryptedMessage = decryptMessage(encryptedMessageBytes, privateKey);
            // Actualizar el área de mensajes de forma segura en el hilo de la interfaz de usuario
            javafx.application.Platform.runLater(() -> messageArea.appendText("Ellos: " + decryptedMessage + "\n"));
        } catch (Exception e) {
            e.printStackTrace();
            // Asegurar que las actualizaciones de la interfaz de usuario se hacen en el hilo correcto
            javafx.application.Platform.runLater(() -> messageArea.appendText("Error al desencriptar el mensaje: " + e.getMessage() + "\n"));
        }
    }


}

