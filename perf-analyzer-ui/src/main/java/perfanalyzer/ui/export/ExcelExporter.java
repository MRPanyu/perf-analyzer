package perfanalyzer.ui.export;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import perfanalyzer.core.model.PerfStatisticsGroup;
import perfanalyzer.core.model.PerfStatisticsNode;
import perfanalyzer.core.model.PerfStatisticsTimedGroup;

public class ExcelExporter {

	private static final String[] HEADERS = new String[] { "层级", "方法名", "执行次数", "成功次数", "异常次数", "总耗时", "总耗时(除子节点)",
			"平均每次耗时", "平均每次耗时(除子节点)", "最大单次耗时", "最大单次耗时(除子节点)", "成功总耗时", "成功总耗时(除子节点)", "成功次均耗时", "成功次均耗时(除子节点)",
			"成功最大耗时", "成功最大耗时(除子节点)", "异常总耗时", "异常总耗时(除子节点)", "异常次均耗时", "异常次均耗时(除子节点)", "异常最大耗时", "异常最大耗时(除子节点)" };

	private static final String[] PROPERTIES = new String[] { "level", "name", "executeCount", "successCount",
			"errorCount", "totalUseTime", "totalUseTimeExcludeChildren", "avgUseTime", "avgUseTimeExcludeChildren",
			"maxUseTime", "maxUseTimeExcludeChildren", "successTotalUseTime", "successTotalUseTimeExcludeChildren",
			"successAvgUseTime", "successAvgUseTimeExcludeChildren", "successMaxUseTime",
			"successMaxUseTimeExcludeChildren", "errorTotalUseTime", "errorTotalUseTimeExcludeChildren",
			"errorAvgUseTime", "errorAvgUseTimeExcludeChildren", "errorMaxUseTime", "errorMaxUseTimeExcludeChildren" };

	private Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>();

	public void exportSheet(Workbook wb, PerfStatisticsTimedGroup group) {
		try {
			String sheetName = new SimpleDateFormat("yyyyMMddHHmm").format(group.getStatisticsStartTime());
			Sheet sheet = wb.createSheet(sheetName);
			writeHeader(wb, sheet, group);
			writeData(wb, sheet, group);
			for (int i = 0; i < HEADERS.length; i++) {
				sheet.autoSizeColumn(i);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void writeHeader(Workbook wb, Sheet sheet, PerfStatisticsGroup group) throws Exception {
		Row row = sheet.createRow(0);
		for (int i = 0; i < HEADERS.length; i++) {
			String header = HEADERS[i];
			CellStyle style = headerStyle(wb);
			Cell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(header);
		}
		sheet.setRowSumsBelow(false);
	}

	private CellStyle headerStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setFillForegroundColor(IndexedColors.SKY_BLUE.index);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		Font font = wb.createFont();
		font.setBold(true);
		style.setFont(font);
		return style;
	}

	private void writeData(Workbook wb, Sheet sheet, PerfStatisticsGroup group) throws Exception {
		AtomicInteger rownum = new AtomicInteger(1);
		for (PerfStatisticsNode node : group.getRootNodes()) {
			writeDataRow(wb, sheet, node, 0, rownum);
		}
	}

	private void writeDataRow(Workbook wb, Sheet sheet, PerfStatisticsNode node, int level, AtomicInteger rownum)
			throws Exception {
		int rn = rownum.getAndIncrement();
		Row row = sheet.createRow(rn);
		for (int i = 0; i < PROPERTIES.length; i++) {
			String property = PROPERTIES[i];
			String value = "";
			if ("level".equals(property)) {
				value = String.valueOf(level);
			} else {
				value = getProperty(node, property);
				if ("name".equals(property)) {
					for (int j = 0; j < level; j++) {
						value = "  " + value;
					}
				}
			}
			CellStyle style = dataStyle(wb);
			Cell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(value);
		}
		if (node.getChildren() != null && !node.getChildren().isEmpty()) {
			for (PerfStatisticsNode child : node.getChildren()) {
				writeDataRow(wb, sheet, child, level + 1, rownum);
			}
			int rnEnd = rownum.get();
			sheet.groupRow(rn + 1, rnEnd);
		}
	}

	private CellStyle dataStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		return style;
	}

	private String getProperty(PerfStatisticsNode node, String property) throws Exception {
		if (propertyDescriptorMap.isEmpty()) {
			BeanInfo beanInfo = Introspector.getBeanInfo(node.getClass());
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				propertyDescriptorMap.put(pd.getName(), pd);
			}
		}
		PropertyDescriptor propertyDescriptor = propertyDescriptorMap.get(property);
		Method readMethod = propertyDescriptor.getReadMethod();
		Object value = readMethod.invoke(node);
		return value == null ? "" : value.toString();
	}

}
