package org.example.chat;
import java.io.IOException;
import java.net.*;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

public class ServerChat {

    private static byte[] incoming = new byte[1024];
    private static final int PORT = 4321;
    private static DatagramSocket socket;
    private static final HashMap<Integer, PublicKey> userPublicKeys = new HashMap<>();
    private static KeyPair serverKeyPair;

    static {
        try {
            socket = new DatagramSocket(PORT);
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            serverKeyPair = keyGen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final InetAddress address;

    static {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println("Server lanzado en el puerto: " + PORT);

        while (true) {
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String message = new String(packet.getData(), 0, packet.getLength());

            if (message.startsWith("init;")) {
                String[] parts = message.split(";");
                if (parts.length == 3) {
                    String userIdentifier = parts[1];
                    String publicKeyString = parts[2];

                    try {
                        byte[] publicBytes = Base64.getDecoder().decode(publicKeyString);
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        PublicKey publicKey = keyFactory.generatePublic(keySpec);

                        userPublicKeys.put(packet.getPort(), publicKey);
                        System.out.println("Clave pública almacenada para el usuario: " + userIdentifier);

                        // Enviar la clave pública del servidor al cliente
                        String serverPublicKeyStr = Base64.getEncoder().encodeToString(serverKeyPair.getPublic().getEncoded());
                        byte[] serverPublicKeyBytes = ("serverKey;" + serverPublicKeyStr).getBytes();
                        DatagramPacket serverKeyPacket = new DatagramPacket(serverPublicKeyBytes, serverPublicKeyBytes.length, packet.getAddress(), packet.getPort());
                        socket.send(serverKeyPacket);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // Procesa mensajes que no son de inicialización
                int userPort = packet.getPort();
                byte[] byteMessage = message.getBytes();

                for (int forwardPort : userPublicKeys.keySet()) {
                    if (forwardPort != userPort) {
                        DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, address, forwardPort);
                        try {
                            socket.send(forward);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
}