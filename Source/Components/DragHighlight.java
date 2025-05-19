package Source.Components;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DragHighlight {
    private double startX, startY;
    private Rectangle selectionRect;
    private boolean dragging = false;

    public DragHighlight(Pane pane) {
        selectionRect = new Rectangle();
        selectionRect.setFill(Color.YELLOW);
        selectionRect.setOpacity(0.5);
        selectionRect.setStroke(Color.GOLD);
        selectionRect.setVisible(false);

        pane.getChildren().add(selectionRect);

        pane.setOnMousePressed(e -> {
            startX = e.getX();
            startY = e.getY();

            selectionRect.setX(startX);
            selectionRect.setY(startY);
            selectionRect.setWidth(0);
            selectionRect.setHeight(0);
            selectionRect.setVisible(true);

            dragging = true;
            e.consume();
        });

        pane.setOnMouseDragged(e -> {
            double x = Math.min(startX, e.getX());
            double y = Math.min(startY, e.getY());
            double width = Math.abs(e.getX() - startX);
            double height = Math.abs(e.getY() - startY);

            selectionRect.setX(x);
            selectionRect.setY(y);
            selectionRect.setWidth(width);
            selectionRect.setHeight(height);
            
            e.consume();
        });

        pane.setOnMouseReleased(_ -> {
            if (!dragging) return;
            dragging = false;
        });
    }

    public Rectangle getSelectionRect() {
        return selectionRect;
    }

    public boolean isSelectionVisibleAndValid() {
        return selectionRect.isVisible() && selectionRect.getWidth() > 0 && selectionRect.getHeight() > 0;
    }

    public void hideSelection() {
        selectionRect.setVisible(false);
        selectionRect.setWidth(0);
        selectionRect.setHeight(0);
    }
}
