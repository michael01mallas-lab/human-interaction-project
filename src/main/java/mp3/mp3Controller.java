package mp3;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;

import javafx.scene.media.Media;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.awt.SystemColor.desktop;


public class mp3Controller implements Initializable {

    @FXML
    private Pane pane;
    @FXML
    private Label songLabel, volumeLabel, timeLabel;
    @FXML
    private Button deleteButton,playbutton, pauseButton, resetButton, previousButton, nextButton, addFileButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumSlider;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private ToggleButton autoPlaybutton, shuffleButton;
    @FXML
    private ListView<String> listOfSongs;


    private Media media;
    private MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;

    private ArrayList<File> songs;

    private int songNumber;//song playing
    private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Font.loadFont(getClass().getResourceAsStream("/fonts/3270NerdFont-Condensed.ttf"), 12);


        songs = new ArrayList<File>();

        directory = new File("src/main/resources/music");

        files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                songs.add(file);
            }
        }

        if (songs.isEmpty()) {
            songLabel.setText("No Audio files available");
            return;
        }
        ObservableList<String> songNames = FXCollections.observableArrayList();
        for (File song : songs) {
            if (song != null) {
                songNames.add(song.getName());
            }
        }
        listOfSongs.setItems(songNames);


        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        timeLabelSetter();
        enableProgressSeek();

        autoPlayButton();


        songLabel.setText("Hello");
        volumeLabel.setText(String.format("Volume: %.1f%%", volumSlider.getValue()));

        for (int i = 0; i < speeds.length; i++) {
            speedBox.getItems().add(Integer.toString(speeds[i]) + "%");
        }
        speedBox.setOnAction(this::changeSpeed);

        volumSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumSlider.getValue() * 0.01);
                volumeLabel.setText(String.format("Volume: %.1f%%", volumSlider.getValue()));
            }
        });



        listOfSongs.setOnMouseClicked(event -> {
            String selectedSong = listOfSongs.getSelectionModel().getSelectedItem();
            int selectedIndex = listOfSongs.getSelectionModel().getSelectedIndex(); // index

            if (selectedSong != null && selectedIndex >= 0) {
                songNumber = selectedIndex;
                pauseMedia();
                media = new Media(songs.get(songNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                playMedia();


                timeLabelSetter();
                autoPlayButton();

                listOfSongs.getSelectionModel().clearSelection();

                listOfSongs.refresh();;

            }
        });

        listOfSongs.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    getStyleClass().remove("playing");  // remove glowing style if present
                } else {
                    setText(item);

                    // Apply style if this cell is the current song
                    if (getIndex() == songNumber) {
                        if (!getStyleClass().contains("playing")) {
                            getStyleClass().add("playing");  // CSS highlights currently playing song
                        }
                    } else {
                        getStyleClass().remove("playing");
                    }
                }
            }
        });


    }


    public void playMedia() {

        changeSpeed(null);
        mediaPlayer.setVolume(volumSlider.getValue() * 0.01);

        mediaPlayer.play();
        songLabel.setText(songs.get(songNumber).getName());

    }

    public void pauseMedia() {
        mediaPlayer.pause();
    }

    public void resetMedia() {
        songProgressBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0));
        timeLabelSetter();


    }

    public void previousMedia() {
        suffleButton();
        if (songNumber > 0) {
            songNumber--;
            mediaPlayer.stop();

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);




            songLabel.setText(songs.get(songNumber).getName());

            playMedia();
            timeLabelSetter();
            listOfSongs.refresh();

            autoPlayButton();

        } else {
            songNumber = songs.size() - 1;
            mediaPlayer.stop();

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);




            songLabel.setText(songs.get(songNumber).getName());

            playMedia();
            timeLabelSetter();
            listOfSongs.refresh();

            autoPlayButton();

        }
    }

    public void nextMedia() {
        suffleButton();
        if (songNumber < songs.size() - 1) {
            songNumber++;
            mediaPlayer.stop();

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);


            songLabel.setText(songs.get(songNumber).getName());

            playMedia();

            timeLabelSetter();
            listOfSongs.refresh();

            autoPlayButton();

        } else {
            songNumber = 0;
            mediaPlayer.stop();

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);




            songLabel.setText(songs.get(songNumber).getName());

            playMedia();
            timeLabelSetter();
            listOfSongs.refresh();

            autoPlayButton();

        }
    }

    public void changeSpeed(ActionEvent event) {
        if (speedBox.getValue() == null) {
            mediaPlayer.setRate(1);
        } else {
            mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
        }
    }


    //adding files
    @FXML
    public void addFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Add Music File");

        File selected = chooser.showOpenDialog(null);
        File currentSongFile = songs.get(songNumber);

        if (selected != null) {
            try {
                File targetDir = new File("src/main/resources/music");
                if (!targetDir.exists()) targetDir.mkdir(); // ensure folder exists

                File targetFile = new File(targetDir, selected.getName());

                if (targetFile.exists()) {
                    songLabel.setText("File already exists!");
                    // After 5 seconds, reset label to current song safely
                    PauseTransition delay = new PauseTransition(Duration.seconds(5));
                    delay.setOnFinished(event2 -> {
                        // Ensure the song still exists
                        if (!songs.isEmpty() && songs.contains(currentSongFile)) {
                            songLabel.setText(currentSongFile.getName());
                        } else if (!songs.isEmpty()) {
                            // fallback
                            songLabel.setText(songs.get(0).getName());
                        }
                    });
                    delay.play();
                    return;
                }


                // Copy file into /music directory
                Files.copy(selected.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Add to list & update player
                songs.add(targetFile);

                // Update label (optional)
                songLabel.setText("Added: " + selected.getName());

                //after 5sec the label changes again

                // After 5 seconds, reset label to current song safely
                PauseTransition delay = new PauseTransition(Duration.seconds(5));
                delay.setOnFinished(event2 -> {
                    // Ensure the song still exists
                    if (!songs.isEmpty() && songs.contains(currentSongFile)) {
                        songLabel.setText(currentSongFile.getName());
                    } else if (!songs.isEmpty()) {
                        // fallback
                        songLabel.setText(songs.get(0).getName());
                    }
                });
                delay.play();

            } catch (IOException e) {
                e.printStackTrace();
            }
            ObservableList<String> songNames = FXCollections.observableArrayList();
            for (File song : songs) songNames.add(song.getName());
            listOfSongs.setItems(songNames);
            listOfSongs.getSelectionModel().clearSelection();
            listOfSongs.refresh();
        }


    }

    //to do fix bug when one song it cant be deleted
    @FXML
   public void deleteSelectedSong(){
        if (songs.isEmpty()) {

            songLabel.setText("No songs to delete!");
            return;
        }

        File currentFile = songs.get(songNumber);
        File fileToDelete = new File(currentFile.getPath());

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;

        }
        media = null;
        System.gc();


        if (currentFile.exists() ) {
            try{
                boolean deleted = fileToDelete.delete();  // attempt deletion
                if (deleted) {
                    System.out.println("File deleted successfully!");
                } else {
                    System.out.println("Failed to delete file. It may be in use or locked.");
                }
            }catch (SecurityException e){
                e.printStackTrace();
            }


        }

        songs.remove(songNumber);

        if (songs.isEmpty()) {
            songNumber = 0;
            songLabel.setText("No Audio files available");
            songProgressBar.setProgress(0);
            listOfSongs.getItems().clear();
            return;
        } else if (songNumber >= songs.size()) {
            songNumber = songs.size() - 1;
        }

        ObservableList<String> songNames = FXCollections.observableArrayList();
        for (File song : songs) {
            songNames.add(song.getName());
        }
        listOfSongs.setItems(songNames);
        listOfSongs.getSelectionModel().clearSelection();
        listOfSongs.refresh();

        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        playMedia();
        timeLabelSetter();

    }


    private void autoPlayButton() {

        if (autoPlaybutton.isSelected()) {

            mediaPlayer.setOnEndOfMedia(() -> {
                nextMedia();
                listOfSongs.refresh();

            });


        }

    }

    private void suffleButton(){
        if (shuffleButton.isSelected()) {

            songNumber = (int) (Math.random() * songs.size());

        }
    }


    private void timeLabelSetter() {
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            double current = newTime.toSeconds();
            double total = media.getDuration().toSeconds();
            songProgressBar.setProgress(current / total);
            timeLabel.setText(String.format("Time: %.1fs / %.1fs", current, total));

        });


    }


    public void volumeSliderScroll() {
        volumSlider.setOnScroll(event -> {
            double delta = event.getDeltaY() > 0 ? 2 : -2;   // scroll up = +2, scroll down = –2
            double newValue = volumSlider.getValue() + delta;

            // keep slider value within 0–100
            newValue = Math.max(0, Math.min(100, newValue));

            volumSlider.setValue(newValue);
        });
    }
    private void enableProgressSeek() {
        songProgressBar.setOnMouseClicked(event -> {
            double mouseX = event.getX();
            double width = songProgressBar.getWidth();

            double percent = mouseX / width;            // convert click → 0–1 range
            percent = Math.max(0, Math.min(1, percent)); // clamp

            Duration seekTime = media.getDuration().multiply(percent);
            mediaPlayer.seek(seekTime);
        });
    }


}
