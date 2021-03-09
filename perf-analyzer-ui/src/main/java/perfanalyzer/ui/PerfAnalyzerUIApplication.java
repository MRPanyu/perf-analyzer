package perfanalyzer.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 性能分析UI程序主应用类
 * 
 * @author panyu
 *
 */
public class PerfAnalyzerUIApplication extends Application {

	public static final String DEFAULT_TITLE = "PerfAnalyzerUI";

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("root.fxml"));
		Parent root = loader.load();
		RootController controller = loader.getController();

		Scene scene = new Scene(root, 1024, 600);

		primaryStage.setTitle(DEFAULT_TITLE);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("analyze.png")));
		primaryStage.setMaximized(true);
		primaryStage.setOnCloseRequest(event -> {
			controller.onClose();
		});
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
