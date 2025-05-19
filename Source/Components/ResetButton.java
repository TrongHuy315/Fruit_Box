package Source.Components;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ResetButton extends Button {
    private Rectangle rect;
    private Text strText;

    public ResetButton(double x, double y, double width, double height, String str, double scale) {
        rect = new Rectangle(width, height);

        rect.setFill(Color.GREEN);
        rect.setStroke(Color.WHITE);

        strText = new Text(str);
        strText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, scale * 0.5));
        strText.setFill(Color.WHITE);

        StackPane stack = new StackPane(rect, strText);

        this.setGraphic(stack);
        this.setStyle("-fx-background-color: transparent;");

        this.setLayoutX(x);
        this.setLayoutY(y);
    }

    public void setOnClickButton(EventHandler<MouseEvent> handler) {
        this.setOnMouseClicked(handler);
    }
}
