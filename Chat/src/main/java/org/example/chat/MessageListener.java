package org.example.chat;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageListener implements Runnable {
    private final ClienteChatController chatController;
    private final BlockingQueue<String> incomingMessages;

    public MessageListener(ClienteChatController chatController) {
        this.chatController = chatController;
        this.incomingMessages = new LinkedBlockingQueue<>();
    }

    public void submitMessage(String encodedMessage) {
        try {
            incomingMessages.put(encodedMessage);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("MessageListener interrumpido mientras ponía un mensaje en la cola.");
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Tomar el próximo mensaje de la cola, bloquea hasta que haya uno disponible
                String encodedMessage = incomingMessages.take();
                // Envía el mensaje al controlador para desencriptar y mostrar
                javafx.application.Platform.runLater(() -> chatController.onMessageReceived(encodedMessage));
            }
        } catch (InterruptedException e) {
            // Manejo de la interrupción del hilo, limpieza si es necesario
            Thread.currentThread().interrupt();
            System.out.println("MessageListener interrumpido durante la espera de nuevos mensajes.");
        }
    }
}
