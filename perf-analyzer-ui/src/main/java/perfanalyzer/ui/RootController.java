package perfanalyzer.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import perfanalyzer.core.io.PerfIOFileImpl;
import perfanalyzer.core.model.PerfStatisticsGroup;
import perfanalyzer.core.model.PerfStatisticsNode;

public class RootController {

	@FXML
	protected GridPane root;
	@FXML
	protected ListView<String> listViewGroups;
	@FXML
	protected TreeTableView<PerfStatisticsNode> treeTableNodes;
	@FXML
	protected TreeTableColumn<PerfStatisticsNode, Object> treeTableColumnName;

	protected File file;

	protected List<PerfStatisticsGroup> groups;

	protected PerfStatisticsGroup selectedGroup;

	@FXML
	public void initialize() {
		// 左侧列表选择事件
		listViewGroups.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onGroupSelectionChange(newValue);
			}
		});
		// treeTableNodes添加tooltip效果
		for (TreeTableColumn<PerfStatisticsNode, ?> column : treeTableNodes.getColumns()) {
			String text = column.getText();
			Label label = new Label(text);
			label.setTooltip(new Tooltip(text));
			column.setText("");
			column.setGraphic(label);
		}
		// name列添加tooltip效果
		treeTableColumnName.setCellFactory(
				new Callback<TreeTableColumn<PerfStatisticsNode, Object>, TreeTableCell<PerfStatisticsNode, Object>>() {
					@Override
					public TreeTableCell<PerfStatisticsNode, Object> call(
							TreeTableColumn<PerfStatisticsNode, Object> p) {
						return new TreeTableCell<PerfStatisticsNode, Object>() {
							@Override
							public void updateItem(Object t, boolean empty) {
								super.updateItem(t, empty);
								if (t == null) {
									setTooltip(null);
									setText(null);
								} else {
									Tooltip tooltip = new Tooltip();
									tooltip.setText(t.toString());
									setTooltip(tooltip);
									setText(t.toString());
								}
							}
						};
					}
				});
	}

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
		renderListViewGroups();
	}

	private void renderListViewGroups() throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		List<String> items = groups.stream()
				.map(g -> fmt.format(g.getStatisticsStartTime()) + " (" + g.getRootNodes().size() + ")")
				.collect(Collectors.toList());
		listViewGroups.getItems().setAll(items);
	}

	public void onGroupSelectionChange(String value) {
		try {
			if (value == null) {
				selectedGroup = null;
			} else {
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date d = fmt.parse(value);
				for (PerfStatisticsGroup group : groups) {
					if (group.getStatisticsStartTime() == d.getTime()) {
						selectedGroup = group;
						break;
					}
				}
			}
			renderTreeTableNodes();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void renderTreeTableNodes() throws Exception {
		if (selectedGroup == null) {
			treeTableNodes.setRoot(null);
		} else {
			PerfStatisticsNode root = new PerfStatisticsNode("root", "root", null);
			TreeItem<PerfStatisticsNode> rootItem = new TreeItem<PerfStatisticsNode>(root);
			for (PerfStatisticsNode node : selectedGroup.getRootNodes()) {
				buildTree(rootItem, node);
			}
			treeTableNodes.setRoot(rootItem);
		}
	}

	private void buildTree(TreeItem<PerfStatisticsNode> parentItem, PerfStatisticsNode node) {
		TreeItem<PerfStatisticsNode> item = new TreeItem<PerfStatisticsNode>(node);
		parentItem.getChildren().add(item);
		for (PerfStatisticsNode child : node.getChildren()) {
			buildTree(item, child);
		}
	}

}
