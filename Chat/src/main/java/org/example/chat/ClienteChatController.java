package org.example.chat;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.*;

public class ClienteChatController {
    @FXML
    private TextArea messageArea;

    @FXML
    private TextField inputBox;

    private DatagramSocket socket;
    private InetAddress address;
    private static final int SERVER_PORT = 4321;
    private static final String identifier = "Juan"; // Este valor podría ser más dinámico según el usuario

    public ClienteChatController() {
        try {
            socket = new DatagramSocket(); // Inicializar el socket
            address = InetAddress.getByName("localhost"); // Dirección del servidor
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace(); // Manejar excepciones adecuadamente
        }
    }

    @FXML
    private void handleSendMessage() {
        String messageText = inputBox.getText().trim();

        if (!messageText.isEmpty()) {
            // Formatear mensaje con identificador
            String formattedMessage = identifier + ";" + messageText;

            // Convertir a bytes
            byte[] messageBytes = formattedMessage.getBytes();

            // Crear y enviar paquete
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, SERVER_PORT);
            try {
                socket.send(packet);

                // Opcional: Actualizar la interfaz de usuario con el mensaje enviado
                messageArea.appendText("Tú: " + messageText + "\n");

                // Limpiar el campo de texto
                inputBox.clear();
            } catch (IOException e) {
                e.printStackTrace(); // Manejar excepciones adecuadamente
            }
        }
    }
}
