package org.example.correoelectronico;

import javafx.fxml.FXML;

import java.io.Serializable;

public class Correo implements Serializable {
    private String remitente;
    @FXML
    private String destinatario;
    private String mensaje;

    public Correo(String remitente, String destinatario, String mensaje) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
    }

    public String getRemitente() {
        return remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getMensaje() {
        return mensaje;
    }
}
