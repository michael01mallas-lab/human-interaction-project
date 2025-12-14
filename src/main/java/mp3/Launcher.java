package mp3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;


public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mp3MainScreen.fxml"));
        Parent root = fxmlLoader.load();

        // Load custom font
        Font.loadFont(getClass().getResourceAsStream("/fonts/3270NerdFont-Condensed.ttf"), 12);

        // Create scene
        Scene scene = new Scene(root, 800, 500);

        // Attach CSS
        scene.getStylesheets().add(getClass().getResource("/mp3/mp3Style.css").toExternalForm());

        // Set stage properties
        stage.setTitle("MP3 Player");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Handle window close
        stage.setOnCloseRequest((WindowEvent event) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
