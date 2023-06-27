package perfanalyzer.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import perfanalyzer.core.io.FilePerfInput;
import perfanalyzer.core.model.NodePath;
import perfanalyzer.core.model.NodeType;
import perfanalyzer.core.model.PerfStatisticsNode;
import perfanalyzer.core.model.PerfStatisticsTimedGroup;
import perfanalyzer.ui.export.ExcelExporter;

/**
 * 主界面对应的Controller
 * 
 * @author panyu
 *
 */
public class RootController {

	protected static String[] levelColors = { "#880015", "#ED1C24", "#FF7F27", "#FFF200", "#22B14C", "#00A2E8",
			"#3F48CC", "#A349A4" };

	@FXML
	protected GridPane root;
	@FXML
	protected TextField txtFilter;
	@FXML
	protected HBox toolBarHBox;
	@FXML
	protected ToolBar toolBarSpacer;
	@FXML
	protected ListView<String> listViewGroups;
	@FXML
	protected TreeTableView<PerfStatisticsNode> treeTableNodes;
	@FXML
	protected TreeTableColumn<PerfStatisticsNode, Object> treeTableColumnName;
	@FXML
	protected ContextMenu contextMenu;

	protected File file;

	protected List<PerfStatisticsTimedGroup> groups;

	protected PerfStatisticsTimedGroup selectedGroup;

	protected Clipboard clipboard = Clipboard.getSystemClipboard();

	protected ExecutorService executorService = Executors.newSingleThreadExecutor();

	@FXML
	public void initialize() {
		// 工具条初始化
		HBox.setHgrow(toolBarSpacer, Priority.ALWAYS);
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
									setStyle(
											"-fx-border-color: white; -fx-border-width: 0 0 0 6; -fx-padding: 0 0 0 -6");
								} else {
									Tooltip tooltip = new Tooltip();
									tooltip.setText(t.toString());
									setTooltip(tooltip);
									setText(t.toString());
									PerfStatisticsNode node = getTreeTableRow().getItem();
									if (node == null) {
										setStyle(
												"-fx-border-color: white; -fx-border-width: 0 0 0 6; -fx-padding: 0 0 0 -6");
									} else {
										NodePath path = node.getPath();
										int level = 0;
										while (path.getParentPath() != null) {
											level++;
											path = path.getParentPath();
										}
										int lv = level % levelColors.length;
										String levelColor = levelColors[lv];
										setStyle("-fx-border-color: " + levelColor
												+ "; -fx-border-width: 0 0 0 6; -fx-padding: 0 0 0 -6");
									}
								}
							}
						};
					}
				});
	}

	/** 关闭事件，由Main类中注册到窗口关闭操作中 */
	public void onClose() {
		executorService.shutdownNow();
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
		} catch (InvalidClassException e) {
			e.printStackTrace();
			showAlert(AlertType.ERROR, "读取文件格式不兼容，可能时以前版本perf-analyzer-agent产生的文件");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 导出Excel(全部)事件 */
	@FXML
	public void onExportAll(ActionEvent event) {
		exportExcel(false);
	}

	/** 导出Excel(当前查看)事件 */
	@FXML
	public void onExportVisible(ActionEvent event) {
		exportExcel(true);
	}

	private void exportExcel(boolean exportVisible) {
		Stage stage = (Stage) root.getScene().getWindow();
		if (groups == null) {
			showAlert(AlertType.WARNING, "请先打开性能分析文件");
			return;
		}
		if (exportVisible && (treeTableNodes.getRoot() == null || treeTableNodes.getRoot().getChildren().isEmpty())) {
			showAlert(AlertType.WARNING, "当前未显示任何节点");
			return;
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("导出Excel文件");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Excel文件(*.xlsx)", "*.xlsx"),
				new ExtensionFilter("所有文件(*.*)", "*.*"));
		file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			final Alert progress = showProgress("正在导出，请稍候...");
			executorService.submit(() -> {
				try (Workbook wb = new XSSFWorkbook(); FileOutputStream fout = new FileOutputStream(file)) {
					ExcelExporter exporter = new ExcelExporter(wb);
					if (exportVisible) {
						exporter.exportSheet(selectedGroup, treeTableNodes.getRoot());
					} else {
						for (PerfStatisticsTimedGroup group : groups) {
							exporter.exportSheet(group, null);
						}
					}
					wb.write(new FileOutputStream(file));
					Platform.runLater(() -> {
						progress.setResult(ButtonType.FINISH);
						progress.close();
						showAlert(AlertType.INFORMATION, "导出完成");
					});
				} catch (Exception e) {
					e.printStackTrace();
					Platform.runLater(() -> {
						progress.setResult(ButtonType.FINISH);
						progress.close();
						showAlert(AlertType.ERROR, "导出异常：" + e.getClass().getName() + ": " + e.getMessage());
					});
				}
			});
		}
	}

	/** 过滤根节点 */
	@FXML
	public void onTxtFilterChange(Event event) {
		try {
			renderTreeTableNodes();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 全部展开 */
	@FXML
	public void onExpandAll(ActionEvent event) {
		TreeItem<?> root = treeTableNodes.getRoot();
		if (root != null) {
			for (TreeItem<?> child : root.getChildren()) {
				setExpandRecursive(child, true);
			}
		}
	}

	/** 全部收起 */
	@FXML
	public void onCollapseAll(ActionEvent event) {
		TreeItem<?> root = treeTableNodes.getRoot();
		if (root != null) {
			for (TreeItem<?> child : root.getChildren()) {
				setExpandRecursive(child, false);
			}
		}
	}

	private void setExpandRecursive(TreeItem<?> treeItem, boolean expanded) {
		for (TreeItem<?> child : treeItem.getChildren()) {
			setExpandRecursive(child, expanded);
		}
		treeItem.setExpanded(expanded);
	}

	/** 复制名称 */
	@FXML
	public void onCopyName(ActionEvent event) {
		TreeItem<PerfStatisticsNode> selectedItem = treeTableNodes.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			PerfStatisticsNode node = selectedItem.getValue();
			String name = node.getName();
			copyToClipboard(name);
		}
	}

	/** 复制整行内容 */
	@FXML
	public void onCopyRow(ActionEvent event) {
		TreeItem<PerfStatisticsNode> selectedItem = treeTableNodes.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			StringBuilder sb = new StringBuilder();
			for (TreeTableColumn<PerfStatisticsNode, ?> col : treeTableNodes.getColumns()) {
				String text = ((Label) col.getGraphic()).getText(); // 为了增加tooltip效果，col本身的text是空的
				Object value = col.getCellData(selectedItem);
				sb.append(text).append(": ").append(value).append("\n");
			}
			sb.deleteCharAt(sb.length() - 1);
			copyToClipboard(sb.toString());
		}
	}

	private void copyToClipboard(String text) {
		ClipboardContent content = new ClipboardContent();
		content.putString(text);
		clipboard.setContent(content);
	}

	public void loadData(File file) throws Exception {
		Stage stage = (Stage) root.getScene().getWindow();
		FilePerfInput input = new FilePerfInput(file);
		groups = input.readAll();
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
				for (PerfStatisticsTimedGroup group : groups) {
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
			String filter = txtFilter.getText();
			NodePath rootPath = NodePath.getInstance("_root", NodeType.METHOD, null); // 虚拟根节点方便显示
			PerfStatisticsNode root = new PerfStatisticsNode(rootPath);
			TreeItem<PerfStatisticsNode> rootItem = new TreeItem<PerfStatisticsNode>(root);
			List<PerfStatisticsNode> foundNodes = new ArrayList<>();
			for (PerfStatisticsNode rootNode : selectedGroup.getRootNodes()) {
				foundNodes.addAll(rootNode.find(filter));
			}
			for (PerfStatisticsNode node : foundNodes) {
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

	private Optional<ButtonType> showAlert(AlertType type, String message) {
		Stage stage = (Stage) root.getScene().getWindow();
		Alert alert = new Alert(type, message);
		alert.initOwner(stage);
		return alert.showAndWait();
	}

	private Alert showProgress(String message) {
		Stage stage = (Stage) root.getScene().getWindow();
		Alert alert = new Alert(AlertType.NONE, message);
		alert.initOwner(stage);
		alert.show();
		return alert;
	}

}
