package org.example.chat;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.*;

public class ChatServer {
    private int port;
    /* private Set<Socket> clientSockets = new HashSet<>();*/
    private Map<Socket, PrintWriter> clientOutputs = new HashMap<>();
    private KeyPair serverKeyPair;
    private final Set<Socket> clientSockets = Collections.synchronizedSet(new HashSet<>());
    private final HashMap<Socket, PublicKey> clientPublicKeys = new HashMap<>();

    public ChatServer(int port) {
        this.port = port;
    }

    // Método para reenviar mensajes a todos los clientes excepto al emisor
    public void broadcastMessage(String message, Socket senderSocket) {
        System.out.println("Reenviando mensaje a todos los clientes excepto al emisor...");
        synchronized (clientSockets) {
            for (Socket socket : clientSockets) {
                if (!socket.equals(senderSocket)) { // No reenviar al emisor
                    PrintWriter out = clientOutputs.get(socket); // Obtener el PrintWriter asociado al socket
                    if (out != null) {
                        out.println(message); // Reenviar el mensaje
                        System.out.println("Mensaje reenviado a: " + socket.getInetAddress().getHostAddress());
                    } else {
                        System.err.println("PrintWriter no encontrado para el cliente: " + socket.getInetAddress().getHostAddress());
                    }
                }
            }
        }
    }


    public void start() {
        try {


            // Generar el par de claves para el servidor
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            serverKeyPair = keyPairGenerator.genKeyPair();

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Servidor iniciado en el puerto " + port + "... esperando clientes.");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                    ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
// Es importante flush() después de inicializar ObjectOutputStream para asegurar que el encabezado se envíe.
                    outputStream.flush();
                    //ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

                    clientSockets.add(clientSocket);
                    new Thread(new ClientHandler(clientSocket, this)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error al inicializar el generador de claves RSA: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ChatServer server;
        private Decryptor decryptor = new Decryptor();

        public ClientHandler(Socket clientSocket, ChatServer server) {
            this.clientSocket = clientSocket;
            this.server = server;
        }

        @Override
        public void run() {
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                outputStream.flush(); // Envía el encabezado
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());


                PublicKey clientPublicKey = (PublicKey) inputStream.readObject();

                // Esperar y recibir la clave pública del cliente
                synchronized (server.clientPublicKeys) {
                    server.clientPublicKeys.put(clientSocket, clientPublicKey);
                }

                // El BufferedReader se elimina porque la lectura se hará a través de ObjectInputStream
                String inputLine;
                while ((inputLine = (String) inputStream.readObject()) != null) {
                    System.out.println("Mensaje cifrado recibido: " + inputLine);

                    // Decodificar el mensaje de Base64 a bytes
                    byte[] encryptedMessageBytes = Base64.getDecoder().decode(inputLine);

                    // Descifrar el mensaje recibido usando la clase Decryptor
                    String decryptedMessage = decryptor.decrypt(encryptedMessageBytes, server.serverKeyPair.getPrivate());

                    System.out.println("Mensaje descifrado: " + decryptedMessage);

                    // Reenviar el mensaje descifrado a todos los clientes (excepto al emisor)
                    server.broadcastMessage(decryptedMessage, clientSocket);
                }
            } catch (EOFException e) {
                System.out.println("Fin de la conexión con el cliente: " + clientSocket);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error en la conexión con el cliente: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Error al descifrar el mensaje: " + e.getMessage());
                e.printStackTrace();
            } finally {
                server.clientSockets.remove(clientSocket);
                synchronized (server.clientPublicKeys) {
                    server.clientPublicKeys.remove(clientSocket);
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Cliente desconectado.");
            }
        }
    }


    public static void main(String[] args) {

        ChatServer server = new ChatServer(12345);
        new Thread(server::start).start();
    }


}