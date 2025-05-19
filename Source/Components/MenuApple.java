package Source.Components;

import javafx.animation.RotateTransition;
import javafx.geometry.VPos;
import javafx.scene.Group; // Thêm import Group
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;        // Thêm import Font
import javafx.scene.text.FontWeight;  // Thêm import FontWeight
import javafx.scene.text.Text;        // Thêm import Text
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class MenuApple {
    // private Path appleShape; // Thay Path bằng Group
    private Group appleGroup;   // Group để chứa tất cả các phần của quả táo
    private Text numberText; // Thêm đối tượng Text
    private RotateTransition rotate;

    /**
     * Tạo một đối tượng quả táo cho menu.
     * @param centerX Tọa độ X của tâm quả táo (ước lượng)
     * @param centerY Tọa độ Y của tâm quả táo (ước lượng)
     * @param scale   Kích thước tổng thể của quả táo
     */
    public MenuApple(double centerX, double centerY, double scale, int number) {
        // --- Vẽ thân táo (Apple Body) ---
        Path appleBody = new Path();
        double bodyRadius = scale * 0.9; // Bán kính phần thân, nhỏ hơn scale một chút
        double topIndentY = centerY - bodyRadius * 0.3; // Độ lõm phía trên
        double bottomY = centerY + bodyRadius;          // Điểm thấp nhất
        double sideBulgeX = bodyRadius * 1.0;         // Độ phình ra hai bên
        double topControlY = centerY - bodyRadius * 0.8;  // Điểm kiểm soát cho phần cong phía trên
        double bottomControlY = centerY + bodyRadius * 1.0; // Điểm kiểm soát cho phần cong phía dưới

        // Bắt đầu từ điểm lõm phía trên
        appleBody.getElements().add(new MoveTo(centerX, topIndentY));

        // Vẽ nửa bên trái của quả táo
        appleBody.getElements().add(new CubicCurveTo(
                centerX - sideBulgeX, topControlY,      // Điểm kiểm soát 1 (ra ngoài, lên trên)
                centerX - sideBulgeX, bottomControlY,   // Điểm kiểm soát 2 (ra ngoài, xuống dưới)
                centerX, bottomY                        // Điểm kết thúc (chính giữa đáy)
        ));

        // Vẽ nửa bên phải của quả táo
        appleBody.getElements().add(new CubicCurveTo(
                centerX + sideBulgeX, bottomControlY,   // Điểm kiểm soát 3 (đối xứng với 2)
                centerX + sideBulgeX, topControlY,      // Điểm kiểm soát 4 (đối xứng với 1)
                centerX, topIndentY                     // Điểm kết thúc (quay lại điểm bắt đầu)
        ));

        appleBody.setFill(Color.RED);           // Màu thân táo
        appleBody.setStroke(Color.DARKRED);     // Màu viền thân táo
        appleBody.setStrokeWidth(scale * 0.05); // Độ dày viền tỉ lệ với kích thước

        // --- Vẽ cuống táo (Stem) ---
        // Hình chữ nhật nhỏ màu nâu phía trên vết lõm
        Rectangle stem = new Rectangle(
                centerX - scale * 0.08, centerY - bodyRadius * 0.6, // Vị trí x, y
                scale * 0.16, bodyRadius * 0.5                  // Chiều rộng, chiều cao
        );
        stem.setFill(Color.SADDLEBROWN); // Màu cuống (nâu)

        // --- Vẽ lá táo (Leaf) ---
        Path leaf = new Path();
        // Xác định các điểm neo và kiểm soát cho lá (vị trí tương đối với tâm)
        double leafStartX = centerX + scale * 0.05;
        double leafStartY = centerY - bodyRadius * 0.35;
        double leafEndX = centerX + scale * 0.6;
        double leafEndY = centerY - bodyRadius * 0.7;
        double leafCtrlX = centerX + scale * 0.15; // Điểm kiểm soát đường cong trên
        double leafCtrlY = centerY - bodyRadius * 1.1;
        double leafCtrlX2 = centerX + scale * 0.4; // Điểm kiểm soát đường cong dưới
        double leafCtrlY2 = centerY - bodyRadius * 0.2;

        leaf.getElements().addAll(
            new MoveTo(leafStartX, leafStartY), // Bắt đầu ở gần cuống
            // Đường cong phía trên của lá
            new QuadCurveTo(leafCtrlX, leafCtrlY, leafEndX, leafEndY),
            // Đường cong phía dưới của lá (quay về điểm bắt đầu)
            new QuadCurveTo(leafCtrlX2, leafCtrlY2, leafStartX, leafStartY)
        );
        leaf.setFill(Color.LIMEGREEN);        // Màu lá
        leaf.setStroke(Color.DARKGREEN);    // Màu viền lá
        leaf.setStrokeWidth(scale * 0.03);  // Viền lá mỏng hơn

        // --- Tạo đối tượng Text cho số ---
        numberText = new Text(String.valueOf(number)); // Đặt nội dung text
        numberText.setFont(Font.font("Arial", FontWeight.BOLD, scale * 0.7)); // Chọn Font, đậm, kích thước tỉ lệ với scale
        numberText.setFill(Color.WHITE); // Đặt màu chữ trắng
        numberText.setTextAlignment(TextAlignment.CENTER); // Căn giữa text theo chiều ngang
        numberText.setTextOrigin(VPos.CENTER); // Căn giữa text theo chiều dọc
        numberText.setX(centerX - scale * 0.2); // Đặt vị trí X tại tâm ngang của táo
        numberText.setY(centerY + scale * 0.3); // Đặt vị trí Y hơi thấp hơn tâm một chút cho đẹp mắt

        // --- Gom các bộ phận vào Group ---
        // Đặt thân táo ở lớp dưới cùng, sau đó đến cuống và lá
        appleGroup = new Group(appleBody, stem, leaf, numberText);

        // --- Hiệu ứng xoay (Animation) ---
        // Áp dụng hiệu ứng xoay cho cả Group
        rotate = new RotateTransition(Duration.seconds(1.5), appleGroup); // Tăng thời gian cho mượt hơn
        rotate.setByAngle(10); // Giảm góc xoay một chút cho tinh tế hơn
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setAutoReverse(true);
    }

    /**
     * Thêm quả táo vào Pane được chỉ định và bắt đầu hiệu ứng.
     * @param pane Pane để hiển thị quả táo.
     */
    public void render(Pane pane) {
        pane.getChildren().add(appleGroup); // Thêm Group vào Pane
        rotate.play();
    }

    public Node getNode() {
        return appleGroup;
    }
}
