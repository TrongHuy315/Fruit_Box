package Source.Components;

import javafx.scene.control.CheckBox;

public class SoundTick extends CheckBox {
    private String filePath = "./Source/Sound/fruit_box_sound.mp3";
    private SoundPlayer sound;

    public SoundTick(double x, double y, String label) {
        super(label);

        this.setLayoutX(x);
        this.setLayoutY(y);

        sound = new SoundPlayer(filePath);
        this.setOnAction(_ -> handleTick());
    }

    private void handleTick() {
        if (this.isSelected()) {
            sound.play();
        }
        else {
            sound.stop();
        }
    }
}
