package org.example.chat;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.crypto.Cipher;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ClienteChatController {
    @FXML
    private TextArea messageArea;

    @FXML
    private TextField inputBox;

    private DatagramSocket socket;
    private InetAddress address;
    private static final int SERVER_PORT = 4321;
    private static final String identifier = "Edu";
    private ClientCryptoManager cryptoManager;

    // La clave pública del servidor se inicializará al recibir desde el servidor
    private PublicKey serverPublicKey;

    @FXML
    public void initialize() {
        try {
            cryptoManager = new ClientCryptoManager();
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
            HiloCliente clientThread = new HiloCliente(socket, messageArea, cryptoManager, this); // Asegúrate de pasar 'this' al HiloCliente
            clientThread.start();

            String initMessageStr = "init;" + identifier + ";" + cryptoManager.getPublicKeyAsString();
            byte[] initMessage = initMessageStr.getBytes();
            DatagramPacket initializePacket = new DatagramPacket(initMessage, initMessage.length, address, SERVER_PORT);
            socket.send(initializePacket);

        } catch (Exception e) {
            e.printStackTrace(); // Considere una mejor gestión de excepciones
        }
    }

    @FXML
    private void handleSendMessage() {
        String messageText = inputBox.getText().trim();
        if (!messageText.isEmpty()) {
            try {
                String formattedMessage = identifier + ":" + messageText;
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey); // Usa la clave pública del servidor
                byte[] encryptedMessage = cipher.doFinal(formattedMessage.getBytes());
                // Codificar el mensaje cifrado en Base64 antes de enviar
                String encryptedBase64Message = Base64.getEncoder().encodeToString(encryptedMessage);
                // Convertir a bytes para enviar
                byte[] messageBytes = encryptedBase64Message.getBytes();
                DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, SERVER_PORT);
                socket.send(packet);
                messageArea.appendText("Tú: " + messageText + "\n");
                inputBox.clear();
            } catch (Exception e) {
                e.printStackTrace(); // Manejo de errores
            }
        }
    }


    // Método para actualizar la clave pública del servidor
    public void updateServerPublicKey(PublicKey publicKey) {
        this.serverPublicKey = publicKey;
    }
}

