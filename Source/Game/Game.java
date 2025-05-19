package Source.Game;

import java.awt.AWTException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import java.awt.Robot;
import Source.Bot.Bot_1.FruitBoxOptimizer;
import Source.Bot.Bot_1.SubMatrixChoice;
import Source.Components.DragHighlight;
import Source.Components.GameApple;
import Source.Components.GameTime;
import Source.Components.PlayAgainButton;
import Source.Utils.IntersectionUtil;
import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class Game {
    private ArrayList<ArrayList<Integer>> boardData = new ArrayList<>();
    private List<GameApple> displayedApples = new ArrayList<>(); // Để quản lý các Node GameApple
    private static final Random random = new Random();
    private DragHighlight dragHighlightHandler;
    private Pane gamePane; // Lưu trữ pane

    private final int row = 10;
    private final int col = 17;

    private static final int minNum = 1;
    private static final int maxNum = 9;

    private final double board_layout_x = 110; // Vị trí bắt đầu của lưới trên Pane
    private final double board_layout_y = 80;
    private final double apple_visual_scale = 45; // Scale dùng để vẽ/tính toán kích thước táo
    private final double apple_cell_width = 45;  // Chiều rộng của một ô chứa táo (bao gồm táo + khoảng cách)
    private final double apple_cell_height = 45; // Chiều cao của một ô chứa táo

    final double MIN_OVERLAP_PERCENTAGE = 50.0;

    private GameTime time;
    private Label displayLabel;
    private AnimationTimer gameLoop;

    private boolean isGameOver;

    private int currentScore;
    private ScoreDisplay scoreDisplay;
    private PlayAgainButton playAgainButton;

    private Button botPlayButton;
    private volatile boolean isBotPlaying = false; // Cờ để kiểm soát trạng thái bot
    private FruitBoxOptimizer gameBotOptimizer; // Instance của bot

    public Game(Pane pane) {
        this.gamePane = pane;
        dragHighlightHandler = new DragHighlight(pane);

        setupTimeDisplayLabel();

        setupScoreDisplay();
        setupPlayAgainButton();
        setupBotPlayButton();

        setupGameLoop();

        this.gamePane.setOnMouseReleased(_ -> {
            if (isGameOver) {
                dragHighlightHandler.hideSelection();
                return;
            }

            if (dragHighlightHandler.isSelectionVisibleAndValid()) {
                Rectangle selectionRect = dragHighlightHandler.getSelectionRect();
                processSelectionAndRemoveApples(selectionRect);
                dragHighlightHandler.hideSelection();
            }
        });

        int[][] initialEmptyBoardForBot = new int[row][col]; // Ma trận toàn 0
        this.gameBotOptimizer = new FruitBoxOptimizer(initialEmptyBoardForBot);

        pane.widthProperty().addListener((obs, oldVal, newVal) -> updateUILayouts());
        pane.heightProperty().addListener((obs, oldVal, newVal) -> updateUILayouts());
    }

    private void setupBotPlayButton() {
        botPlayButton = new Button("Bot Play");
        // Vị trí ban đầu, sẽ được cập nhật trong updateUILayouts
        botPlayButton.setLayoutX(700); // Giá trị tạm
        botPlayButton.setLayoutY(10);

        botPlayButton.setOnAction(e -> {
            if (isGameOver) return; // Không làm gì nếu game đã over

            if (isBotPlaying) {
                stopBot();
            } else {
                startBot();
            }
        });
        if (!this.gamePane.getChildren().contains(botPlayButton)) {
            this.gamePane.getChildren().add(botPlayButton);
        }
    }

    private void startBot() {
        if (isGameOver || isBotPlaying) return;

        isBotPlaying = true;
        botPlayButton.setText("Stop Bot");

        Task<Void> botTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Robot awtRobot;
                try {
                    awtRobot = new Robot();
                } catch (AWTException ex) {
                    System.err.println("Không thể khởi tạo Robot: " + ex.getMessage());
                    javafx.application.Platform.runLater(() -> stopBot()); // Dừng bot trên UI thread
                    return null;
                }

                double gridScreenOriginX = 0;
                double gridScreenOriginY = 0;

                java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
                double[] screenCoords = new double[2];

                javafx.application.Platform.runLater(() -> {
                    try {
                        if (gamePane.getScene() != null && gamePane.getScene().getWindow() != null) {
                            Bounds paneBoundsInScene = gamePane.localToScene(gamePane.getBoundsInLocal());
                            screenCoords[0] = gamePane.getScene().getWindow().getX() + paneBoundsInScene.getMinX() + board_layout_x;
                            screenCoords[1] = gamePane.getScene().getWindow().getY() + paneBoundsInScene.getMinY() + board_layout_y;
                        } else {
                            System.err.println("Bot: Không thể lấy Scene/Window để tính tọa độ màn hình.");
                            // Có thể đặt giá trị mặc định hoặc ném lỗi
                        }
                    } finally {
                        latch.countDown();
                    }
                });
                latch.await(); // Chờ UI thread hoàn thành
                gridScreenOriginX = screenCoords[0];
                gridScreenOriginY = screenCoords[1];

                if (gridScreenOriginX == 0 && gridScreenOriginY == 0) {
                     System.err.println("Bot: Tọa độ màn hình không hợp lệ. Dừng bot.");
                     javafx.application.Platform.runLater(() -> stopBot());
                     return null;
                }


                while (isBotPlaying && !isGameOver && !Thread.currentThread().isInterrupted()) {
                    int[][] currentBoardForBot = new int[row][col];
                    for (int i = 0; i < row; i++) {
                        for (int j = 0; j < col; j++) {
                            currentBoardForBot[i][j] = boardData.get(i).get(j);
                        }
                    }
                    gameBotOptimizer.updateMatrix(currentBoardForBot);

                    SubMatrixChoice bestChoice = gameBotOptimizer.findBestSubmatrix();

                    if (bestChoice == null) {
                        System.out.println("Bot: Không tìm thấy nước đi nào nữa.");
                        break; // Dừng vòng lặp của bot
                    }

                    gameBotOptimizer.performRobotAction(awtRobot,
                            gridScreenOriginX, gridScreenOriginY,
                            apple_cell_width, apple_cell_height, // Kích thước pixel của ô
                            bestChoice);

                    // Robot đã có delay, nhưng có thể cần thêm ở đây nếu game xử lý chậm
                    Thread.sleep(300); // Tăng/giảm nếu cần

                    if (isGameOver) break;
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (isBotPlaying) {
                    javafx.application.Platform.runLater(() -> stopBot());
                }
                System.out.println("Bot task finished successfully.");
            }

            @Override
            protected void failed() {
                super.failed();
                getException().printStackTrace();
                javafx.application.Platform.runLater(() -> stopBot());
                System.err.println("Bot task failed.");
            }
        };

        new Thread(botTask).start();
    }

    private void stopBot() {
        isBotPlaying = false;
        botPlayButton.setText("Bot Play");
        System.out.println("Bot stopped.");
    }

    private void setupTimeDisplayLabel() {
        displayLabel = new Label("Time: --:--");
        displayLabel.setFont(Font.font("Arial", 20));
        displayLabel.setTextFill(Color.BLACK);
        displayLabel.setLayoutX(10);
        displayLabel.setLayoutY(10);
        
        this.gamePane.getChildren().add(displayLabel);
    }

    private void setupGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isGameOver) {
                    return;
                }

                updateTimerAndCheckFinish();
            }
        };
    }

    private void updateTimerAndCheckFinish() {
        if (time.isFinish()) {
            if (!isGameOver) {
                handleGameOver();
            }

            return;
        }

        // Tính toán thời gian còn lại bằng GIÂY
        long elapsedMillis = System.currentTimeMillis() - time.getGameTime();
        long remainingMillis = time.getDurationTime() - elapsedMillis;

        if (remainingMillis < 0) {
            remainingMillis = 0;
        }
        long secondsLeftOverall = remainingMillis / 1000;

        long displayMinutes = secondsLeftOverall / 60;
        long displaySeconds = secondsLeftOverall % 60;

        displayLabel.setText(String.format("Time: %02d:%02d", displayMinutes, displaySeconds));
    }

    private void setupScoreDisplay() {
        double scoreDisplayX = gamePane.getWidth() - 160; // Căn phải
        if (gamePane.getWidth() == 0) scoreDisplayX = 840;
        double scoreDisplayY = 10;
        double scoreDisplayScale = 100;

        scoreDisplay = new ScoreDisplay(scoreDisplayX, scoreDisplayY, scoreDisplayScale);
        if (!this.gamePane.getChildren().contains(scoreDisplay)) {
            this.gamePane.getChildren().add(scoreDisplay);
        }
        scoreDisplay.hide(); // Ban đầu ẩn
    }

    private void setupPlayAgainButton() {
        double buttonX = gamePane.getWidth() / 2 - 75;
        if (gamePane.getWidth() == 0) buttonX = 425;
        double buttonY = gamePane.getHeight() / 2 + 50;
        if (gamePane.getHeight() == 0) buttonY = 350;

        playAgainButton = new PlayAgainButton("PLAY AGAIN", buttonX, buttonY);
        playAgainButton.setOnClick(_ -> {
            hideGameOverUI();
            startGame();
        });
        if (!this.gamePane.getChildren().contains(playAgainButton)) {
            this.gamePane.getChildren().add(playAgainButton);
        }
        playAgainButton.hide();
    }

    private void handleGameOver() {
        if (isGameOver) return;

        isGameOver = true;
        if (isBotPlaying) {
            stopBot();
        }

        gameLoop.stop();

        clearDisplayedApplesFromPane();

        displayLabel.setText("TIME OVER");

        scoreDisplay.setScore(currentScore);
        scoreDisplay.show();
        playAgainButton.show();
    }

    private void updateUILayouts() {
        double scoreDisplayX = Math.max(10, gamePane.getWidth() - 160);
        scoreDisplay.setLayoutX(scoreDisplayX);
        scoreDisplay.setLayoutY(10);

        // Cập nhật vị trí cho PlayAgainButton
        double buttonX = Math.max(10, gamePane.getWidth() / 2 - playAgainButton.getPrefWidth() / 2);
        double buttonY = Math.max(10, gamePane.getHeight() / 2 + 50);
        playAgainButton.setLayoutX(buttonX);
        playAgainButton.setLayoutY(buttonY);
        
        double botButtonX = Math.max(10, gamePane.getWidth() - 100 - botPlayButton.getWidth()); // Căn phải, cách lề 100

        botPlayButton.setLayoutX(Math.max(10, gamePane.getWidth() - botPlayButton.getWidth() - 10)); // Căn phải, cách lề 10
        botPlayButton.setLayoutY(10);


    }

    public void startGame() {
        isGameOver = false;
        if(isBotPlaying) stopBot();

        currentScore = 0;
        hideGameOverUI();

        initializeBoardData();
        displayBoard();

        if (time == null) {
            time = new GameTime();
        }
        time.startGameTime();

        long initialSeconds = time.getDurationTime() / 1000;
        displayLabel.setText(String.format("Time: %02d:%02d", initialSeconds / 60, initialSeconds % 60));

        if (gameLoop == null) {
            setupGameLoop();
        }
        gameLoop.start();

        javafx.application.Platform.runLater(this::updateUILayouts);
    }

    private void hideGameOverUI() {
        if (scoreDisplay != null) scoreDisplay.hide();
        if (playAgainButton != null) playAgainButton.hide();
    }

    private List<Integer> generateNumbersWithDistribution() {
        List<Integer> numbers = new ArrayList<>(row * col);
        int totalCells = row * col;

        int count1_4, count5, count6_9;

        // Define target counts and ranges
        int minCount1_4 = 80, maxCount1_4 = 90;
        int minCount5 = 15, maxCount5 = 20;
        // Range for count6_9 is derived: [65, 75]

        // We need to pick count1_4 and count5 such that
        // (totalCells - maxCount6_9) <= count1_4 + count5 <= (totalCells - minCount6_9)
        // 170 - 75 = 95
        // 170 - 65 = 105
        // So, 95 <= count1_4 + count5 <= 105

        do {
            count1_4 = random.nextInt(maxCount1_4 - minCount1_4 + 1) + minCount1_4;
            count5 = random.nextInt(maxCount5 - minCount5 + 1) + minCount5;
        } while (!((count1_4 + count5 >= 95) && (count1_4 + count5 <= 105)));

        count6_9 = totalCells - count1_4 - count5;

        // Populate numbers for category 1-4
        for (int i = 0; i < count1_4; i++) {
            numbers.add(random.nextInt(4) + 1); // Generates 1, 2, 3, 4
        }

        // Populate numbers for category 5
        for (int i = 0; i < count5; i++) {
            numbers.add(5);
        }

        // Populate numbers for category 6-9
        for (int i = 0; i < count6_9; i++) {
            numbers.add(random.nextInt(4) + 6); // Generates 6, 7, 8, 9
        }

        return numbers;
    }

    private void initializeBoardData() {
        boardData.clear();
        List<Integer> numbersToPlace = generateNumbersWithDistribution();
        
        // Shuffle the generated numbers to ensure random placement on the board
        Collections.shuffle(numbersToPlace, random);

        int listIndex = 0;
        for (int i = 0; i < row; i++) {
            ArrayList<Integer> sub = new ArrayList<>();
            for (int j = 0; j < col; j++) {
                if (listIndex < numbersToPlace.size()) {
                    sub.add(numbersToPlace.get(listIndex++));
                } else {
                    // Fallback, though this should not happen if generateNumbersWithDistribution is correct
                    System.err.println("Error: Not enough numbers generated for the board. Adding 0.");
                    sub.add(0); 
                }
            }
            boardData.add(sub);
        }
    }

    public void displayBoard() {
        clearDisplayedApplesFromPane();

        double currentY = board_layout_y;
        for (int i = 0; i < row; i++) {
            double currentX = board_layout_x;
            for (int j = 0; j < col; j++) {
                int num = boardData.get(i).get(j);
                if (num != 0) {
                    GameApple apple = new GameApple(currentX, currentY, apple_visual_scale, num, i, j);
                    if (!this.gamePane.getChildren().contains(apple)) {
                        this.gamePane.getChildren().add(apple);
                    }
                    displayedApples.add(apple);
                }
                currentX += apple_cell_width;
            }
            currentY += apple_cell_height;
        }
    }

    private void clearDisplayedApplesFromPane() {
        for (GameApple apple : displayedApples) {
            this.gamePane.getChildren().remove(apple);
        }
        displayedApples.clear();
    }

    private void processSelectionAndRemoveApples(Rectangle selectionRect) {
        Bounds selectionBounds = selectionRect.getBoundsInParent();

        List<GameApple> applesToRemove = new ArrayList<>();

        int sumOfSelectedNumbers = 0;

        for (GameApple apple : displayedApples) {
            if (apple == null) continue;

            Bounds appleBounds = apple.getBoundsInParent();

            double overlapPercentage = IntersectionUtil.getOverlapPercentageOfFirst(appleBounds, selectionBounds);
            if (overlapPercentage >= MIN_OVERLAP_PERCENTAGE) {
                applesToRemove.add(apple);
                sumOfSelectedNumbers += apple.getNumber();
            }
        }

        if (sumOfSelectedNumbers == 10 && !applesToRemove.isEmpty()) {
            for (GameApple apple : applesToRemove) {
                gamePane.getChildren().remove(apple);
                boardData.get(apple.getRowIndex()).set(apple.getColIndex(), 0);
                currentScore += 1;
            }
            displayedApples.removeAll(applesToRemove);
        }
    }
}
