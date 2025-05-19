package Source.Main;

import Source.Menu.Menu;
import Source.Rim.Rim;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private double width = 1000;
    private double height = 600;
    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();

        Rim rim = new Rim(0, 0, width, height);
        rim.render(pane);

        Menu menu = new Menu(pane, width, height);
        rim.resetButtonSolve(pane, menu.getResetNodes());

        Scene scene = new Scene(pane, width, height);
        primaryStage.setTitle("Fruit Box");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
