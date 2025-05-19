// File: Source/Components/GameApple.java
package Source.Components;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class GameApple extends Group {
    private int number;
    private int rowIndex;
    private int colIndex;
    private double visualScale;

    /**
     * Tạo một đối tượng GameApple với đồ họa chi tiết.
     * @param layoutX Vị trí X của góc trên trái GameApple trên Pane
     * @param layoutY Vị trí Y của góc trên trái GameApple trên Pane
     * @param scale   Kích thước tổng thể của quả táo (ví dụ: chiều rộng/cao của vùng bao)
     * @param num     Số hiển thị trên quả táo
     * @param r_idx   Chỉ số hàng trên board
     * @param c_idx   Chỉ số cột trên board
     */

    public GameApple(double layoutX, double layoutY, double scale, int num, int r_idx, int c_idx) {
        this.number = num;
        this.rowIndex = r_idx;
        this.colIndex = c_idx;
        this.visualScale = scale;

        double centerX_relative = scale / 2;
        double centerY_relative = scale / 2;

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

        // --- Tạo đối tượng Text cho số ---
        Text numberText = new Text(String.valueOf(number));
        numberText.setFont(Font.font("Arial", FontWeight.BOLD, scale * 0.5)); // Kích thước font có thể cần điều chỉnh
        numberText.setFill(Color.BLACK); // Màu chữ (có thể là trắng hoặc đen tùy nền)
        numberText.setTextAlignment(TextAlignment.CENTER);
        numberText.setTextOrigin(VPos.CENTER);

        // Căn chỉnh Text. Lấy bounds sau khi setFont.
        double textWidth = numberText.getLayoutBounds().getWidth();
        double textHeight = numberText.getLayoutBounds().getHeight(); // Có thể dùng để căn Y chính xác hơn
        numberText.setX(centerX_relative - textWidth / 2);
        numberText.setY(centerY_relative + textHeight / 4); // Thử nghiệm vị trí Y

        // Thêm các bộ phận vào Group này (GameApple)
        this.getChildren().addAll(appleBody, stem, numberText);

        // Đặt vị trí X, Y cho toàn bộ GameApple (Group này) trên Pane cha
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
    }

    public int getNumber() { return number; }
    public int getRowIndex() { return rowIndex; }
    public int getColIndex() { return colIndex; }
    public double getVisualScale() { return visualScale; }
}
