package Source.Rim;

import java.util.ArrayList;
import java.util.List;

import Source.Components.ResetButton;
import Source.Components.SoundTick;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class Rim {
    private Rectangle rect;
    ResetButton resetButton;
    SoundTick soundTick;
    private final double sizeRect = 40;

    private List<Node> keepNodes = new ArrayList<>();

    private final double rs_x = 120;
    private final double rs_y = 565;
    private final double rs_width = 50;
    private final double rs_height = 20;
    private final String reset = "Reset";
    private final double reset_scale = 30;

    private final double st_x = 800;
    private final double st_y = 570;
    private final String soundString = "Sound";

    public Rim(double x, double y, double width, double height) {
        rect = new Rectangle(x, y, width, height);

        rect.setFill(Color.WHITE);
        rect.setStroke(Color.GREEN);
        rect.setStrokeWidth(sizeRect);
        rect.setStrokeType(StrokeType.INSIDE);

        resetButton = new ResetButton(rs_x, rs_y, rs_width, rs_height, reset, reset_scale);
        soundTick = new SoundTick(st_x, st_y, soundString);

        keepNodes.addAll(List.of(
            rect, resetButton, soundTick
        ));
    }

    public void render(Pane pane) {
        pane.getChildren().addAll(keepNodes);
    }

    public void resetButtonSolve(Pane pane, List<Node> resetNodes) {
        resetButton.setOnClickButton(_ -> {
            pane.getChildren().retainAll(keepNodes);
            pane.getChildren().addAll(resetNodes);
        });
    }
}
