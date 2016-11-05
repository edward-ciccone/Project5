package application;

import application.layout.BallPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {		
		BallPane pane = new BallPane(10);
        primaryStage.setScene(new Scene(pane, 600, 600));
        primaryStage.setTitle("Bean Machine");
        primaryStage.show();
        pane.dropBall();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
