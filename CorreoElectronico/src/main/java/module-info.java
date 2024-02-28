module org.example.correoelectronico {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.correoelectronico to javafx.fxml;
    exports org.example.correoelectronico;
}