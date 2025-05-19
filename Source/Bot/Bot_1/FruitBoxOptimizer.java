package Source.Bot.Bot_1;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FruitBoxOptimizer {
    private int[][] matrix;
    private int rows;
    private int cols;

    public FruitBoxOptimizer(int[][] initialMatrix) {
        this.rows = initialMatrix.length;
        this.cols = initialMatrix[0].length;
        this.matrix = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.matrix[i][j] = initialMatrix[i][j];
            }
        }
    }

    public void updateMatrix(int[][] currentActualBoard) {
        if (currentActualBoard.length != this.rows || currentActualBoard[0].length != this.cols) {
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

    public SubMatrixChoice findBestSubmatrix() {
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
                                currentSum += matrix[i][j];
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

        validChoices.sort(Comparator
                .comparingInt(SubMatrixChoice::getNonZeroElements)
                .thenComparingInt(SubMatrixChoice::getR1)
                .thenComparingInt(SubMatrixChoice::getC1)
                .thenComparingInt(SubMatrixChoice::getR2)
                .thenComparingInt(SubMatrixChoice::getC2));

        return validChoices.get(0);
    }

    public void performRobotAction(Robot robotInstance,
                                   double gridScreenOriginX, double gridScreenOriginY,
                                   double cellPixelWidth, double cellPixelHeight,
                                   SubMatrixChoice bestChoiceToPlay) {
        if (bestChoiceToPlay == null) return;

        int screenX1 = (int) (gridScreenOriginX + bestChoiceToPlay.getC1() * cellPixelWidth);
        int screenY1 = (int) (gridScreenOriginY + (bestChoiceToPlay.getR1() + 1) * cellPixelHeight);

        int screenX2 = (int) (gridScreenOriginX + bestChoiceToPlay.getC2() * cellPixelWidth + cellPixelWidth);
        int screenY2 = (int) (gridScreenOriginY + (bestChoiceToPlay.getR2() + 1) * cellPixelHeight + cellPixelHeight);

        robotInstance.delay(150);
        robotInstance.mouseMove(screenX1, screenY1);
        robotInstance.delay(100);
        robotInstance.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robotInstance.delay(100);
        robotInstance.mouseMove(screenX2, screenY2);
        robotInstance.delay(100);
        robotInstance.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robotInstance.delay(150);

        System.out.println(bestChoiceToPlay.getC1());
        System.out.println(bestChoiceToPlay.getR1());
        System.out.println(bestChoiceToPlay.getC2());
        System.out.println(bestChoiceToPlay.getR2());
    }
}
