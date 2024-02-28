package org.example.correoelectronico;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class Login {
    @FXML
    private Button btnInicio;

    @FXML
    private TextField userName;

    @FXML
    private PasswordField userPassword;

    private Datos datos = new Datos();


    @FXML
    void validarAcceso(ActionEvent event) throws IOException, ClassNotFoundException {
        //Variables para cambiar Stage
        Button button = (Button) event.getSource(); // Obtiene el botón que desencadenó el evento
        String fxmlResource = null; // Ruta al archivo FXML que se cargará
        String stageTitle = null;  // Título de la nueva ventana
        //____________________________________
        String usuario = userName.getText();
        String password = userPassword.getText();

        boolean accesoPermitido = false;

        for (Usuario u : datos.getListaUsuarios()) {
            if (u.getUserName().equals(usuario) && u.getPassword().equals(password)) {
                accesoPermitido = true;
                break;
            }
        }

        if (accesoPermitido) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bandeja-entrada.fxml"));
            Parent root = loader.load();
            User user=loader.getController();
            user.recibirCorreo(this.userName.getText());
//            UserController.setUserName("@" + usuario);
//            UserController.establecerDatos(this.datos); // Asegúrate de que 'creados' es correcto

            Stage stage = new Stage();
            stage.setTitle("email");
            stage.setScene(new Scene(root));
            stage.show();
            Button handler_button = (Button) event.getSource();
            Stage stageThis= (Stage) handler_button.getScene().getWindow();
            stageThis.close();
        } else {
            System.out.println("DENEGADO");
            String alerta = "El acceso fue denegado";
            mostrarAlertaError(alerta);
        }

    }

    public void mostrarAlertaError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
