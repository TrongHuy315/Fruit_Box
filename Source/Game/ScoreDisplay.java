// File: src/Source/Components/ScoreDisplay.java
package Source.Game;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle; // Đơn giản hóa bằng hình tròn
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ScoreDisplay extends Group {
    private Text scoreText;
    private Circle backgroundApple;
    private Text labelText; // "Score:"

    public ScoreDisplay(double layoutX, double layoutY, double scale) {
        // Nền quả táo (hình tròn đơn giản)
        backgroundApple = new Circle(scale / 2, scale / 2, scale / 2);
        backgroundApple.setFill(Color.GOLD); // Màu vàng cho điểm
        backgroundApple.setStroke(Color.DARKGOLDENROD);
        backgroundApple.setStrokeWidth(scale * 0.05);

        // Text "Score:"
        labelText = new Text("Score:");
        labelText.setFont(Font.font("Arial", FontWeight.BOLD, scale * 0.25));
        labelText.setFill(Color.BLACK);
        labelText.setTextOrigin(VPos.CENTER);
        labelText.setX(scale / 2 - labelText.getLayoutBounds().getWidth() / 2);
        labelText.setY(scale * 0.35); // Vị trí phía trên điểm số

        // Text hiển thị điểm số
        scoreText = new Text("0"); // Điểm ban đầu
        scoreText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, scale * 0.4));
        scoreText.setFill(Color.BLACK);
        scoreText.setTextAlignment(TextAlignment.CENTER);
        scoreText.setTextOrigin(VPos.CENTER);
        // Cập nhật vị trí X sau khi có text để căn giữa
        updateScoreTextPosition(scale);


        this.getChildren().addAll(backgroundApple, labelText, scoreText);
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
        this.setVisible(false); // Ban đầu ẩn, chỉ hiển thị khi cần
    }

    private void updateScoreTextPosition(double scale) {
        double textWidth = scoreText.getLayoutBounds().getWidth();
        scoreText.setX(scale / 2 - textWidth / 2);
        scoreText.setY(scale * 0.65); // Vị trí điểm số
    }

    public void setScore(int score) {
        scoreText.setText(String.valueOf(score));
        // Cập nhật lại vị trí X vì chiều rộng của text có thể thay đổi
        double scale = backgroundApple.getRadius() * 2; // Lấy lại scale từ bán kính
        updateScoreTextPosition(scale);
    }

    public void show() {
        this.setVisible(true);
    }

    public void hide() {
        this.setVisible(false);
    }
}
