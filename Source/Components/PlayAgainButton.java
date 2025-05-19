// File: src/Source/Components/PlayAgainButton.java
package Source.Components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PlayAgainButton extends Button {
    public PlayAgainButton(String text, double layoutX, double layoutY) {
        super(text);
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
        this.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        this.setVisible(false);
    }

    public void setOnClick(EventHandler<ActionEvent> handler) {
        this.setOnAction(handler);
    }

    public void show() {
        this.setVisible(true);
    }

    public void hide() {
        this.setVisible(false);
    }
}
