package org.example.correoelectronico;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ManagerCorreoView {
    @FXML
    private Label txtDescripcion;

    @FXML
    private Label txtDestinatario;

    @FXML
    private Label txtRemitente;
    public void recibirData(Correo correo){
        this.txtDescripcion.setText(correo.getMensaje());
        this.txtDestinatario.setText(correo.getDestinatario());
        this.txtRemitente.setText(correo.getRemitente());
    }
}
