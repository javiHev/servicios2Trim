package org.example.correoelectronico;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CrearCorreo {
    private String direccion;
    @FXML
    private TextArea contenido;
    private ObjectOutputStream out;

    @FXML
    private TextField destinatario; // Cambiado de MFXTextField a TextField
    private Socket socket;
    private User cliente;
    @FXML
    private Label correoErroneo;



    @FXML
    void enviar(MouseEvent event) {
        if (contenido.getText().isEmpty() || destinatario.getText().isEmpty()) {
            correoErroneo.setText("Los campos no pueden estar vacíos.");
            return;
        }

        try {
            out.writeObject(new Correo(direccion, destinatario.getText(), contenido.getText()));
        } catch (IOException e) {
            correoErroneo.setText("Error al enviar el correo.");
            e.printStackTrace();
        }
    }

    public void recibirData(String mensaje, User cliente, Socket socket) throws IOException {
        this.direccion = mensaje;
        this.cliente = cliente;
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream()); // Inicializado aquí
    }

    public void correoErroneo() {
        Platform.runLater(() -> correoErroneo.setText("Correo incorrecto o inexistente."));
    }
}