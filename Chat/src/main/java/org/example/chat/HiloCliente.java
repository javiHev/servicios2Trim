package org.example.chat;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import java.util.Base64;

public class HiloCliente extends Thread {

    private DatagramSocket socket;
    private byte[] incoming = new byte[1024]; // Asegúrate de que este tamaño sea suficiente para el mensaje cifrado
    private TextArea textArea;
    private ClientCryptoManager cryptoManager;
    private ClienteChatController controller;

    public HiloCliente(DatagramSocket socket, TextArea textArea, ClientCryptoManager cryptoManager, ClienteChatController controller) {
        this.socket = socket;
        this.textArea = textArea;
        this.cryptoManager = cryptoManager;
        this.controller = controller;
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
            try {
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());

                if (message.startsWith("serverKey;")) {
                    // Manejar la recepción de la clave pública del servidor
                    String serverKeyStr = message.substring(10); // Eliminar el prefijo "serverKey;"
                    byte[] serverKeyBytes = Base64.getDecoder().decode(serverKeyStr);
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(serverKeyBytes);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    PublicKey serverKey = keyFactory.generatePublic(keySpec);

                    controller.updateServerPublicKey(serverKey); // Actualiza la clave pública del servidor en el controlador
                } else {
                    // Descifrar el mensaje
                    PrivateKey privateKey = cryptoManager.getPrivateKey();
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.DECRYPT_MODE, privateKey);
                    byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(message)); // Asume que el mensaje cifrado se recibe en Base64

                    String decryptedMessage = new String(decryptedBytes);
                    Platform.runLater(() -> textArea.appendText(decryptedMessage + "\n"));

                    // Mostrar el mensaje cifrado en la consola
                    System.out.println("Mensaje cifrado recibido: " + message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
