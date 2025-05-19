package Source.Components;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundPlayer {
    private MediaPlayer mediaplayer;

    public SoundPlayer(String filePath) {
        Media sound = new Media(new File(filePath).toURI().toString());
        mediaplayer = new MediaPlayer(sound);
    }

    public void stop() {
        mediaplayer.stop();
    }

    public void play() {
        mediaplayer.stop();
        mediaplayer.play();
    }
}
