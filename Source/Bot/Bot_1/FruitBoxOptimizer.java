package Source.Bot.Bot_1; // Hoặc package phù hợp

import java.awt.Robot; // Chỉ import, không tạo instance ở đây trừ khi cần cho việc khác
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FruitBoxOptimizer {
    private int[][] matrix; // Ma trận nội bộ của bot để tính toán
    private int rows;
    private int cols;

    // Constructor này sẽ được dùng để kiểm tra nước đi hoặc khi bot chỉ cần logic
    public FruitBoxOptimizer(int[][] initialMatrix) {
        this.rows = initialMatrix.length;
        this.cols = initialMatrix[0].length;
        this.matrix = new int[rows][cols]; // Tạo bản sao
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.matrix[i][j] = initialMatrix[i][j];
            }
        }
    }

    // Cập nhật ma trận nội bộ của bot với trạng thái game mới nhất
    public void updateMatrix(int[][] currentActualBoard) {
        if (currentActualBoard.length != this.rows || currentActualBoard[0].length != this.cols) {
            // Kích thước thay đổi, cần khởi tạo lại (ít khả năng xảy ra trong game này)
            this.rows = currentActualBoard.length;
            this.cols = currentActualBoard[0].length;
            this.matrix = new int[this.rows][this.cols];
        }
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.matrix[i][j] = currentActualBoard[i][j];
            }
        }
    }


    // Tìm ma trận con tốt nhất dựa trên trạng thái matrix hiện tại của bot
    public SubMatrixChoice findBestSubmatrix() { // Đổi tên thành public để Game có thể gọi kiểm tra
        List<SubMatrixChoice> validChoices = new ArrayList<>();
        for (int r1 = 0; r1 < rows; r1++) {
            for (int c1 = 0; c1 < cols; c1++) {
                for (int r2 = r1; r2 < rows; r2++) {
                    for (int c2 = c1; c2 < cols; c2++) {
                        int currentSum = 0;
                        int nonZeroCount = 0;
                        boolean hasNonZero = false;
                        for (int i = r1; i <= r2; i++) {
                            for (int j = c1; j <= c2; j++) {
                                currentSum += matrix[i][j]; // Sử dụng matrix nội bộ của bot
                                if (matrix[i][j] != 0) {
                                    nonZeroCount++;
                                    hasNonZero = true;
                                }
                            }
                        }
                        if (currentSum == 10 && hasNonZero) {
                            validChoices.add(new SubMatrixChoice(r1, c1, r2, c2, nonZeroCount));
                        }
                    }
                }
            }
        }

        if (validChoices.isEmpty()) {
            return null;
        }

        // TỐI ƯU: Chọn ma trận con có NHIỀU phần tử khác 0 nhất.
        validChoices.sort(Comparator
                .comparingInt(SubMatrixChoice::getNonZeroElements)
                .thenComparingInt(SubMatrixChoice::getR1)
                .thenComparingInt(SubMatrixChoice::getC1)
                .thenComparingInt(SubMatrixChoice::getR2)
                .thenComparingInt(SubMatrixChoice::getC2));

        return validChoices.get(0);
    }

    /**
     * Thực hiện một bước đi của bot bằng cách điều khiển Robot.
     *
     * @param robotInstance      Đối tượng Robot đã được tạo.
     * @param gridScreenOriginX  Tọa độ X trên màn hình của ô (0,0) trong lưới game.
     * @param gridScreenOriginY  Tọa độ Y trên màn hình của ô (0,0) trong lưới game.
     * @param cellPixelWidth     Chiều rộng pixel của một ô.
     * @param cellPixelHeight    Chiều cao pixel của một ô.
     * @param bestChoiceToPlay   Nước đi đã được chọn (từ findBestSubmatrix).
     */
    public void performRobotAction(Robot robotInstance,
                                   double gridScreenOriginX, double gridScreenOriginY,
                                   double cellPixelWidth, double cellPixelHeight,
                                   SubMatrixChoice bestChoiceToPlay) {
        if (bestChoiceToPlay == null) return;

        // Tính tọa độ tâm của ô bắt đầu (r1, c1) trên màn hình
        int screenX1 = (int) (gridScreenOriginX + bestChoiceToPlay.getC1() * cellPixelWidth);
        int screenY1 = (int) (gridScreenOriginY + (bestChoiceToPlay.getR1() + 1) * cellPixelHeight);

        // Tính tọa độ tâm của ô kết thúc (r2, c2) trên màn hình
        int screenX2 = (int) (gridScreenOriginX + bestChoiceToPlay.getC2() * cellPixelWidth + cellPixelWidth);
        int screenY2 = (int) (gridScreenOriginY + (bestChoiceToPlay.getR2() + 1) * cellPixelHeight + cellPixelHeight);

        // System.out.printf("Bot Action: Dragging from screen (%d,%d) to (%d,%d) for choice %s\n",
        // screenX1, screenY1, screenX2, screenY2, bestChoiceToPlay.toString());

        robotInstance.delay(150); // Delay nhỏ trước khi bắt đầu hành động
        robotInstance.mouseMove(screenX1, screenY1);
        robotInstance.delay(100);
        robotInstance.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robotInstance.delay(100); // Giữ chuột một chút
        robotInstance.mouseMove(screenX2, screenY2);
        robotInstance.delay(100);
        robotInstance.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robotInstance.delay(150); // Delay sau hành động để game có thời gian xử lý

        System.out.println(bestChoiceToPlay.getC1());
        System.out.println(bestChoiceToPlay.getR1());
        System.out.println(bestChoiceToPlay.getC2());
        System.out.println(bestChoiceToPlay.getR2());
    }

    // Phương thức này không còn cần thiết nếu Game.java điều khiển vòng lặp và Robot
    // public void playOptimal() { ... }
}
