module org.example.mp3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens mp3 to javafx.fxml;
    exports mp3;
}