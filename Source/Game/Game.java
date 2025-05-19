package Source.Game;

import java.awt.AWTException;
import java.util.ArrayList;
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

                // Tính toán tọa độ gốc của lưới game trên MÀN HÌNH
                // Điều này cần gamePane đã được hiển thị và có parent là Scene và Window
                double gridScreenOriginX = 0;
                double gridScreenOriginY = 0;

                // Cần chạy trên UI thread để lấy tọa độ chính xác
                // Sử dụng CountDownLatch để chờ kết quả từ UI thread
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
                    // 1. Cập nhật trạng thái game hiện tại cho bot
                    int[][] currentBoardForBot = new int[row][col];
                    for (int i = 0; i < row; i++) {
                        for (int j = 0; j < col; j++) {
                            currentBoardForBot[i][j] = boardData.get(i).get(j);
                        }
                    }
                    gameBotOptimizer.updateMatrix(currentBoardForBot);

                    // 2. Bot tìm nước đi tốt nhất
                    SubMatrixChoice bestChoice = gameBotOptimizer.findBestSubmatrix();

                    if (bestChoice == null) {
                        System.out.println("Bot: Không tìm thấy nước đi nào nữa.");
                        break; // Dừng vòng lặp của bot
                    }

                    // 3. Bot thực hiện hành động bằng Robot
                    gameBotOptimizer.performRobotAction(awtRobot,
                            gridScreenOriginX, gridScreenOriginY,
                            apple_cell_width, apple_cell_height, // Kích thước pixel của ô
                            bestChoice);

                    // 4. Chờ một chút để game xử lý sự kiện chuột và cập nhật UI
                    // Robot đã có delay, nhưng có thể cần thêm ở đây nếu game xử lý chậm
                    Thread.sleep(300); // Tăng/giảm nếu cần

                    // Kiểm tra lại isGameOver sau khi bot thực hiện, vì game có thể kết thúc do hết giờ
                    if (isGameOver) break;
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (isBotPlaying) { // Nếu bot tự dừng (ví dụ hết nước đi)
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
        // Vị trí ví dụ cho score display
        double scoreDisplayX = gamePane.getWidth() - 160; // Căn phải
        if (gamePane.getWidth() == 0) scoreDisplayX = 840; // Giá trị mặc định nếu pane chưa có kích thước
        double scoreDisplayY = 10;
        double scoreDisplayScale = 100; // Kích thước của "quả táo điểm"

        scoreDisplay = new ScoreDisplay(scoreDisplayX, scoreDisplayY, scoreDisplayScale);
        if (!this.gamePane.getChildren().contains(scoreDisplay)) {
            this.gamePane.getChildren().add(scoreDisplay);
        }
        scoreDisplay.hide(); // Ban đầu ẩn
    }

    private void setupPlayAgainButton() {
        // Vị trí ví dụ cho nút Play Again, có thể ở giữa màn hình khi game over
        double buttonX = gamePane.getWidth() / 2 - 75; // Căn giữa
        if (gamePane.getWidth() == 0) buttonX = 425; // Giá trị mặc định
        double buttonY = gamePane.getHeight() / 2 + 50;
        if (gamePane.getHeight() == 0) buttonY = 350; // Giá trị mặc định

        playAgainButton = new PlayAgainButton("PLAY AGAIN", buttonX, buttonY);
        playAgainButton.setOnClick(_ -> {
            // Hành động khi bấm nút Play Again
            hideGameOverUI(); // Ẩn các UI của game over
            startGame();      // Bắt đầu lại game
        });
        if (!this.gamePane.getChildren().contains(playAgainButton)) {
            this.gamePane.getChildren().add(playAgainButton);
        }
        playAgainButton.hide(); // Ban đầu ẩn
    }

    private void handleGameOver() {
        if (isGameOver) return;

        isGameOver = true;
        if (isBotPlaying) { // Nếu bot đang chạy thì dừng nó lại
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
        // Cập nhật vị trí cho ScoreDisplay
        double scoreDisplayX = Math.max(10, gamePane.getWidth() - 160);
        scoreDisplay.setLayoutX(scoreDisplayX);
        scoreDisplay.setLayoutY(10);

        // Cập nhật vị trí cho PlayAgainButton
        double buttonX = Math.max(10, gamePane.getWidth() / 2 - playAgainButton.getPrefWidth() / 2);
        double buttonY = Math.max(10, gamePane.getHeight() / 2 + 50);
        playAgainButton.setLayoutX(buttonX);
        playAgainButton.setLayoutY(buttonY);
        
        // Cập nhật vị trí cho BotPlayButton
        double botButtonX = Math.max(10, gamePane.getWidth() - 100 - botPlayButton.getWidth()); // Căn phải, cách lề 100
        // Hoặc một vị trí cố định khác
        // double botButtonX = 10; // Căn trái gần Time
        // botPlayButton.setLayoutX(botButtonX);
        // botPlayButton.setLayoutY(40); // Dưới Time
        // Vị trí hiện tại:
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

    private void initializeBoardData() {
        boardData.clear();
        for (int i = 0; i < row; i++) {
            ArrayList<Integer> sub = new ArrayList<>();
            for (int j = 0; j < col; j++) {
                sub.add(randomNumber());
            }
            boardData.add(sub);
        }
    }

    private static int randomNumber() {
        return random.nextInt(maxNum - minNum + 1) + minNum;
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
