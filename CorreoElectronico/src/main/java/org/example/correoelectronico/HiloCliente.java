package org.example.correoelectronico;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
public class HiloCliente extends Thread{
    private ObjectInputStream in;
    private Socket socket;
    private User cliente;
    private volatile boolean activado = true; // Usamos volatile para asegurar la visibilidad entre hilos

    public HiloCliente(Socket socket, User cliente) {
        this.socket = socket;
        this.cliente = cliente;
        try {
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error al inicializar el stream de entrada: " + e.getMessage());
            activado = false; // Desactivamos el hilo si no podemos inicializar los streams
        }
    }

    @Override
    public void run() {
        while (activado) {
            try {
                System.out.println("Escuchando...");
                Correo correoRecibido = (Correo) in.readObject();
                if (correoRecibido != null) {
                    System.out.println("Recibiendo correo...");
                    cliente.recibirMensaje(correoRecibido);
                } else {
                    System.out.println("Correo inválido recibido.");
                    cliente.modificar();
                }
            } catch (IOException e) {
                System.err.println("Error de conexión: " + e.getMessage());
                activado = false; // Desactivamos el hilo en caso de error de IO
            } catch (ClassNotFoundException e) {
                System.err.println("Clase Correo no encontrada: " + e.getMessage());
            }
        }

        cerrarRecursos();
    }

    private void cerrarRecursos() {
        try {
            if (in != null) {
                in.close();
            }
            socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar los recursos: " + e.getMessage());
        }
    }
}
