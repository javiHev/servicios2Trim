package org.example.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClienteChat extends Application {
    /*
    *            IMPORTANTE
    *
    *   1.Ejecuta antes la clase ServerChat
    *
    *   2.Ejecuta la primera instancia de esta clase.
    *
    *   3.Ejecuta una segunda instancia de la clase cliente (Esto se hace yendo a Run > Edit Configurations
    *   dale un nombre como ClienteChat2 y asegurate que el campo Main class sea esta clase (Cliente Chat)
    *
    *   4.Puedes cambiar el nombre en la clase ClienteChatController al ejecutar la segunda instancia para simular
    *    una conversacion entre dos personas distintas.
    * */

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("chat_ui.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}