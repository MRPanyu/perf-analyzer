package perfanalyzer.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PerfAnalyzerUIApplication extends Application {

	public static final String DEFAULT_TITLE = "PerfAnalyzerUI";
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("root.fxml"));

		Scene scene = new Scene(root, 1024, 600);

		primaryStage.setTitle(DEFAULT_TITLE);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
