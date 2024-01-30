module org.example.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml.crypto;


    opens org.example.chat to javafx.fxml;
    exports org.example.chat;

}