package Source.Components;

import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class PlayApple extends Button {
    /**
     * @param targetX Vị trí X mong muốn của PlayApple trong Pane cha
     * @param targetY Vị trí Y mong muốn của PlayApple trong Pane cha
     * @param scale   Kích thước tổng thể của quả táo
     * @param str     Chữ hiển thị trên quả táo
     */
    public PlayApple(double targetX, double targetY, double scale, String str) {
        double centerX_relative = scale * 0.5;
        double centerY_relative = scale * 0.5;

        // --- Vẽ thân táo (Apple Body) ---
        Path appleBody = new Path();
        double bodyRadius = scale * 0.45;
        double topIndentY = centerY_relative - bodyRadius * 0.3;
        double bottomY = centerY_relative + bodyRadius;
        double sideBulgeX = bodyRadius * 1.0;
        double topControlY = centerY_relative - bodyRadius * 0.8;
        double bottomControlY = centerY_relative + bodyRadius * 1.0;

        appleBody.getElements().add(new MoveTo(centerX_relative, topIndentY));
        appleBody.getElements().add(new CubicCurveTo(
                centerX_relative - sideBulgeX, topControlY,
                centerX_relative - sideBulgeX, bottomControlY,
                centerX_relative, bottomY
        ));
        appleBody.getElements().add(new CubicCurveTo(
                centerX_relative + sideBulgeX, bottomControlY,
                centerX_relative + sideBulgeX, topControlY,
                centerX_relative, topIndentY
        ));
        appleBody.setFill(Color.RED);
        appleBody.setStroke(Color.DARKRED);
        appleBody.setStrokeWidth(scale * 0.05);

        // --- Vẽ cuống táo (Stem) ---
        Rectangle stem = new Rectangle(
                centerX_relative - scale * 0.08, centerY_relative - bodyRadius * 0.6,
                scale * 0.16, bodyRadius * 0.5
        );
        stem.setFill(Color.SADDLEBROWN);

        // --- Vẽ lá táo (Leaf) ---
        Path leaf = new Path();
        double leafStartX = centerX_relative + scale * 0.05;
        double leafStartY = centerY_relative - bodyRadius * 0.35;
        double leafEndX = centerX_relative + scale * 0.6;
        double leafEndY = centerY_relative - bodyRadius * 0.7;
        double leafCtrlX = centerX_relative + scale * 0.15;
        double leafCtrlY = centerY_relative - bodyRadius * 1.1;
        double leafCtrlX2 = centerX_relative + scale * 0.4;
        double leafCtrlY2 = centerY_relative - bodyRadius * 0.2;
        leaf.getElements().addAll(
            new MoveTo(leafStartX, leafStartY),
            new QuadCurveTo(leafCtrlX, leafCtrlY, leafEndX, leafEndY),
            new QuadCurveTo(leafCtrlX2, leafCtrlY2, leafStartX, leafStartY)
        );
        leaf.setFill(Color.LIMEGREEN);
        leaf.setStroke(Color.DARKGREEN);
        leaf.setStrokeWidth(scale * 0.03);

        // --- Text ---
        Text strText = new Text(str);
        strText.setFont(Font.font("Arial", FontWeight.BOLD, scale * 0.2));
        strText.setFill(Color.WHITE);
        strText.setTextAlignment(TextAlignment.CENTER);
        strText.setTextOrigin(VPos.CENTER);

        strText.setX(centerX_relative - strText.getLayoutBounds().getWidth() / 2);
        strText.setY(centerY_relative + scale * 0.1);

        // --- Gom các bộ phận vào Group để làm graphic ---
        Group appleGraphic = new Group(appleBody, stem, leaf, strText);

        this.setGraphic(appleGraphic);

        this.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        this.setLayoutX(targetX);
        this.setLayoutY(targetY);
    }

    /**
     * @param pane
     */
    public void render(Pane pane) {
        pane.getChildren().add(this);
    }

    public void setOnClickButton(EventHandler<MouseEvent> handler) {
        this.setOnMouseClicked(handler);
    }
}