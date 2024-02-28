package org.example.correoelectronico;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
public class HandlerClientThread extends Thread{
    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private ArrayList<Correo> emailList;
    private HashMap<String, Socket> clientAddresses;

    public HandlerClientThread(Socket socket, ArrayList<Correo> emails, HashMap<String, Socket> addresses) throws IOException {
        this.clientSocket = socket;
        this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
        this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        this.emailList = emails;
        this.clientAddresses = addresses;
    }

    @Override
    public void run() {
        try {
            while (!clientSocket.isClosed()) {
                Correo email = (Correo) inputStream.readObject();
                System.out.println(email.getDestinatario());

                if (email.getDestinatario().trim().equalsIgnoreCase("server")) {
                    handleServerEmail(email);
                } else {
                    handleClientEmail(email);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                inputStream.close();
                outputStream.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    private void handleServerEmail(Correo email) throws IOException {
        clientAddresses.putIfAbsent(email.getRemitente(), clientSocket);

        for (Correo eachEmail : emailList) {
            if (eachEmail.getRemitente().equalsIgnoreCase(email.getRemitente()) ||
                    eachEmail.getDestinatario().equalsIgnoreCase(email.getRemitente())) {
                outputStream.writeObject(eachEmail);
            }
        }
        outputStream.writeObject(null); // Indicate end of emails
    }

    private void handleClientEmail(Correo email) throws IOException {
        if (!clientAddresses.containsKey(email.getDestinatario())) {
            System.out.println("Correo no existente");
            outputStream.writeObject(null);
            return;
        }

        emailList.add(email);
        outputStream.writeObject(email);
        System.out.println(email.getDestinatario() + " destinatario");

        ObjectOutputStream destOutStream = new ObjectOutputStream(clientAddresses.get(email.getDestinatario()).getOutputStream());
        destOutStream.writeObject(email);
    }
}
