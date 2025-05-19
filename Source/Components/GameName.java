package Source.Components;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class GameName extends Group {
    private final String minStr = "GAME";
    private final String maxStr = "FRUIT BOX";

    private Text textMin;
    private Text textMax;

    public GameName(double x, double y) {
        textMin = new Text(minStr);
        textMin.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        textMin.setFill(Color.RED);
        textMin.setX(0);
        textMin.setY(0);

        textMax = new Text(maxStr);
        textMax.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        textMax.setFill(Color.BLUE);
        textMax.setX(0);
        textMax.setY(40);

        this.setLayoutX(x);
        this.setLayoutY(y);

        this.getChildren().addAll(textMin, textMax);
    }

    public void render(Pane pane) {
        pane.getChildren().addAll(this);
    }
}
