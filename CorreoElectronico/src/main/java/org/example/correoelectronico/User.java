package org.example.correoelectronico;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class User {

        private String correo;
        private HiloCliente hilo;
        private String host = "localhost";
        private int puerto = 5000;
        @FXML
        private ScrollPane contenido;
        private CrearCorreo controllerCrearCorreo;
        @FXML
        private Label name;

        @FXML
        private CheckBox enviados;
        @FXML
        private CheckBox recibidos;
        private VBox vBox;
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private ArrayList<Correo> correosRecibidos = new ArrayList<>();
        private ArrayList<Correo> correosEnviados = new ArrayList<>();
   /*     @FXML
        private TextField mensaje;
        @FXML
        private TextField correoText;*/

        public void recibirCorreo(String correo) throws IOException, ClassNotFoundException {
            this.correo = correo;
            name.setText(this.correo);
            socket = new Socket(host, puerto);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new Correo(this.correo, "server", null));
            in = new ObjectInputStream(socket.getInputStream());

            Correo correoRecibido;
            while ((correoRecibido = (Correo) in.readObject()) != null) {
                if (correoRecibido.getDestinatario().equalsIgnoreCase(this.correo)) {
                    correosRecibidos.add(correoRecibido);
                } else if (correoRecibido.getRemitente().equalsIgnoreCase(this.correo)) {
                    correosEnviados.add(correoRecibido);
                }
            }
            System.out.println("acabado de recuperar");
            hilo = new HiloCliente(socket, this);
            hilo.start();
            System.out.println(correosEnviados.size());
            System.out.println(correosRecibidos.size());
            cargarEnviados();
        }

        public void cargarEnviados() {
            Platform.runLater(() -> {
                enviados.setSelected(true);
                recibidos.setSelected(false);
                vBox = new VBox();
                contenido.setContent(vBox);
                if (correosEnviados.isEmpty()) return;

                correosEnviados.forEach(correo -> {
                    HBox hBox = new HBox();
                    Label remitente = new Label("Yo");
                    Label texto = new Label(correo.getMensaje());
                    Button btn = new Button("Ver");
                    btn.setId(String.valueOf(correosEnviados.indexOf(correo)));
                    btn.setOnMouseClicked(event -> verEnviado(correo));

                    hBox.getChildren().addAll(remitente, texto, btn);
                    vBox.getChildren().add(hBox);
                });
            });
        }

        public void cargarRecibidos() {
            Platform.runLater(() -> {
                recibidos.setSelected(true);
                enviados.setSelected(false);
                vBox = new VBox();
                contenido.setContent(vBox);
                if (correosRecibidos.isEmpty()) return;

                correosRecibidos.forEach(correo -> {
                    HBox hBox = new HBox();
                    Label remitente = new Label(correo.getRemitente());
                    Label texto = new Label(correo.getMensaje());
                    Button btn = new Button("Ver");
                    btn.setId(String.valueOf(correosRecibidos.indexOf(correo)));
                    btn.setOnMouseClicked(event -> verRecibido(correo));

                    hBox.getChildren().addAll(remitente, texto, btn);
                    vBox.getChildren().add(hBox);
                });
            });
        }

        private void verEnviado(Correo correo) {
            mostrarVistaCorreo(correo);
        }

        private void verRecibido(Correo correo) {
            mostrarVistaCorreo(correo);
        }

        private void mostrarVistaCorreo(Correo correo) {
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("correo-view.fxml"));
                    Parent root = loader.load();
                    ManagerCorreoView controller = loader.getController();
                    controller.recibirData(correo);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        @FXML
        public void enviar() throws IOException {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("correo.fxml"));
            Parent root = loader.load();
            CrearCorreo controller = loader.getController();
            controller.recibirData(correo, this, socket);
            stage.setScene(new Scene(root));
            stage.show();
        }
    public void modificar() {
        this.controllerCrearCorreo.correoErroneo();
    }

        public void recibirMensaje(Correo correo) {
            Platform.runLater(() -> {
                ArrayList<Correo> listaCorrespondiente = correo.getRemitente().equalsIgnoreCase(this.correo) ? correosEnviados : correosRecibidos;
                listaCorrespondiente.add(correo);
                if (correo.getRemitente().equalsIgnoreCase(this.correo) && enviados.isSelected()) {
                    cargarEnviados();
                } else if (!correo.getRemitente().equalsIgnoreCase(this.correo) && recibidos.isSelected()) {
                    cargarRecibidos();
                }
            });
        }
    }

