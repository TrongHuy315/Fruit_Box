package Source.Menu;

import java.util.ArrayList;
import java.util.List;

import Source.Components.GameName;
import Source.Components.MenuApple;
import Source.Components.PlayApple;
import Source.Game.Game;
import Source.Rim.Rim;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class Menu {
    List<Node> resetNodes = new ArrayList<>();
    PlayApple apple;
    GameName name;
    Rim rim;

    private final int quantity = 10;

    double menuApple_x = 500;
    double menuApple_y = 300;
    double scaleMenuApple = 50;

    double playApple_x = 150;
    double playApple_y = 250;
    double scalePlayApple = 200;
    String str = "PLAY";

    double name_x = 50;
    double name_y = 70;

    public Menu(Pane pane, double width, double height) {
        addMenuApple(pane);
        addPlayApple(pane);
        addGameName(pane);      
    }

    private void addMenuApple(Pane pane) {        
        int adder_x = 0;
        int adder_y = 0;
        for (int i = 1; i < quantity; i++) {
            if (i == 6) {
                adder_x = 0;
                adder_y = 80;
            }
            MenuApple apple = new MenuApple(menuApple_x + adder_x, menuApple_y + adder_y, scaleMenuApple, i);
            apple.render(pane);
            resetNodes.add(apple.getNode());

            adder_x += 80;
        }
    }

    private void addPlayApple(Pane pane) {
        apple = new PlayApple(playApple_x, playApple_y, scalePlayApple, str);
        apple.render(pane);

        resetNodes.add(apple);

        apple.setOnClickButton(_ -> {
            pane.getChildren().removeAll(resetNodes);

            Game game = new Game(pane);
            game.startGame();
        });
    }

    private void addGameName(Pane pane) {
        name = new GameName(name_x, name_y);
        name.render(pane);

        resetNodes.add(name);
    }

    public List<Node> getResetNodes() {
        return resetNodes;
    }
}
