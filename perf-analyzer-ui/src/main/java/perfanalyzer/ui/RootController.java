package perfanalyzer.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import perfanalyzer.ui.export.ExcelExporter;

/**
 * 主界面对应的Controller
 * 
 * @author panyu
 *
 */
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

	/** 打开文件事件 */
	@FXML
	public void onOpen(ActionEvent event) {
		Stage stage = (Stage) root.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("打开文件");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("性能记录文件(*.prec)", "*.prec"),
				new ExtensionFilter("所有文件(*.*)", "*.*"));
		file = fileChooser.showOpenDialog(stage);
		try {
			if (file != null) {
				loadData(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 导出Excel(所选)事件 */
	@FXML
	public void onExportSelected(ActionEvent event) {
		Stage stage = (Stage) root.getScene().getWindow();
		if (groups == null) {
			showAlert(AlertType.WARNING, "请先打开性能分析文件");
			return;
		}
		if (selectedGroup == null) {
			showAlert(AlertType.WARNING, "请先选择某一分钟的记录");
			return;
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("导出Excel文件");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Excel文件(*.xlsx)", "*.xlsx"),
				new ExtensionFilter("所有文件(*.*)", "*.*"));
		file = fileChooser.showSaveDialog(stage);
		try {
			if (file != null) {
				try (Workbook wb = new XSSFWorkbook(); FileOutputStream fout = new FileOutputStream(file)) {
					ExcelExporter exporter = new ExcelExporter();
					exporter.exportSheet(wb, selectedGroup);
					wb.write(new FileOutputStream(file));
				}
				showAlert(AlertType.INFORMATION, "导出完成");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(AlertType.ERROR, "导出异常：" + e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/** 导出Excel(全部)事件 */
	@FXML
	public void onExportAll(ActionEvent event) {
		Stage stage = (Stage) root.getScene().getWindow();
		if (groups == null) {
			showAlert(AlertType.WARNING, "请先打开性能分析文件");
			return;
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("导出Excel文件");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Excel文件(*.xlsx)", "*.xlsx"),
				new ExtensionFilter("所有文件(*.*)", "*.*"));
		file = fileChooser.showSaveDialog(stage);
		try {
			if (file != null) {
				try (Workbook wb = new XSSFWorkbook(); FileOutputStream fout = new FileOutputStream(file)) {
					ExcelExporter exporter = new ExcelExporter();
					for (PerfStatisticsGroup group : groups) {
						exporter.exportSheet(wb, group);
					}
					wb.write(new FileOutputStream(file));
				}
				showAlert(AlertType.INFORMATION, "导出完成");
			}
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(AlertType.ERROR, "导出异常：" + e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void loadData(File file) throws Exception {
		Stage stage = (Stage) root.getScene().getWindow();
		PerfIOFileImpl perfIO = new PerfIOFileImpl(file);
		groups = perfIO.loadPerfStatisticsGroups();
		if (groups != null && !groups.isEmpty()) {
			stage.setTitle(PerfAnalyzerUIApplication.DEFAULT_TITLE + " - " + file.getCanonicalPath());
			renderListViewGroups();
		} else {
			showAlert(AlertType.ERROR, "文件格式不正确或文件已损坏");
		}
	}

	private void renderListViewGroups() throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		List<String> items = groups.stream()
				.map(g -> fmt.format(g.getStatisticsStartTime()) + " (" + g.getRootNodes().size() + ")")
				.collect(Collectors.toList());
		listViewGroups.getItems().setAll(items);
	}

	/** 左侧列表选择某个统计信息组事件 */
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
			PerfStatisticsNode root = new PerfStatisticsNode("root", "root");
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

	private void showAlert(AlertType type, String message) {
		Stage stage = (Stage) root.getScene().getWindow();
		Alert alert = new Alert(type, message);
		alert.initOwner(stage);
		alert.showAndWait();
	}

}
