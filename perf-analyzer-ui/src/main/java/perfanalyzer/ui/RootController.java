package perfanalyzer.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import perfanalyzer.core.io.PerfIOFileImpl;
import perfanalyzer.core.model.PerfStatisticsGroup;
import perfanalyzer.core.model.PerfStatisticsNode;

public class RootController {

	@FXML
	protected GridPane root;
	@FXML
	protected ListView<String> groupsList;
	@FXML
	protected TreeTableView<PerfStatisticsNode> dataTreeTable;

	protected File file;

	protected List<PerfStatisticsGroup> groups;

	@FXML
	public void onOpen(ActionEvent event) {
		Stage stage = (Stage) root.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Perf Analyzer Record Data", "*.prec"),
				new ExtensionFilter("All Files", "*.*"));
		file = fileChooser.showOpenDialog(stage);
		try {
			if (file != null) {
				stage.setTitle(PerfAnalyzerUIApplication.DEFAULT_TITLE + " - " + file.getCanonicalPath());
				loadData(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadData(File file) throws Exception {
		PerfIOFileImpl perfIO = new PerfIOFileImpl(file);
		groups = perfIO.loadPerfStatisticsGroups();
		renderGroupList();
	}

	private void renderGroupList() throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		List<String> items = groups.stream().map(g -> fmt.format(g.getStatisticsStartTime()))
				.collect(Collectors.toList());
		groupsList.getItems().setAll(items);
	}

}
