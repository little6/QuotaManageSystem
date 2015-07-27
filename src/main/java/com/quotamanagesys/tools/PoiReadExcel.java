package com.quotamanagesys.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;

import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.Expose;

@Component
public class PoiReadExcel {

	@DataProvider
	public String getHtml() {
		String htmlString = "";
		try {
			PoiReadExcel poire = new PoiReadExcel();
			String path = "C:\\DC_\\�ӳع����X��X�¹ؼ�ҵ������ָ����������.xls";
			htmlString = poire.read(path).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlString;
	}
	
	/**
	 * ��ȡ Excel ��ʾҳ��.
	 * 
	 * @param properties
	 * @return
	 * @throws Exception
	 */
	
	
	public StringBuffer read(String fileName) throws Exception {
		HSSFSheet sheet = null;
		StringBuffer lsb = new StringBuffer();
		String excelFileName = fileName;

		lsb.append("<!DOCTYPE html><html><head>"
				+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" />"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"jq/css/style.css\" />"
				+ "</head><body><div>");

		try {
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(
					excelFileName)); // ������Excel
			for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
				sheet = workbook.getSheetAt(sheetIndex);// �����е�sheet
				String sheetName = workbook.getSheetName(sheetIndex); // sheetName
				if (workbook.getSheetAt(sheetIndex) != null) {
					sheet = workbook.getSheetAt(sheetIndex);// ��ò�Ϊ�յ����sheet
					if (sheet != null) {
						int firstRowNum = sheet.getFirstRowNum(); // ��һ��
						int lastRowNum = sheet.getLastRowNum(); // ���һ��
						// ����Table��ͷ
						lsb.append("<table class=\"gridView\" id=\"table1\" width=\"100%\" style=\"border:1px solid #000;border-width:1px 0 0 1px;margin:2px 0 2px 0;border-collapse:collapse;\">");
						lsb.append("<thead>");
						for (int rowNum = firstRowNum; rowNum <= 1; rowNum++) {
							if (sheet.getRow(rowNum) != null) {// ����в�Ϊ�գ�
								HSSFRow row = sheet.getRow(rowNum);
								short firstCellNum = row.getFirstCellNum(); // ���еĵ�һ����Ԫ��
								short lastCellNum = row.getLastCellNum(); // ���е����һ����Ԫ��
								int height = (int) (row.getHeight() / 15.625); // �еĸ߶�
								lsb.append("<tr height=\""
										+ height
										+ "\" style=\"border:1px solid #000;border-width:0 1px 1px 0;margin:2px 0 2px 0;\">");
								for (short cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) { // ѭ�����е�ÿһ����Ԫ��
									HSSFCell cell = row.getCell(cellNum);
									if (cell != null) {
										if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
											continue;
										} else {
											StringBuffer tdStyle = new StringBuffer(
													"<td style=\"border:1px solid #000; border-width:0 1px 1px 0;margin:2px 0 2px 0; ");
											HSSFCellStyle cellStyle = cell
													.getCellStyle();
											HSSFPalette palette = workbook
													.getCustomPalette(); // ��HSSFPalette��������ɫ�Ĺ��ʱ�׼��ʽ
											HSSFColor hColor = palette
													.getColor(cellStyle
															.getFillForegroundColor());
											HSSFColor hColor2 = palette
													.getColor(cellStyle
															.getFont(workbook)
															.getColor());

											String bgColor = convertToStardColor(hColor);// ������ɫ
											short boldWeight = cellStyle
													.getFont(workbook)
													.getBoldweight(); // �����ϸ
											short fontHeight = (short) (cellStyle
													.getFont(workbook)
													.getFontHeight() / 2); // �����С
											String fontColor = convertToStardColor(hColor2); // ������ɫ
											if (bgColor != null
													&& !"".equals(bgColor
															.trim())) {
												tdStyle.append(" background-color:"
														+ bgColor + "; ");
											}
											if (fontColor != null
													&& !"".equals(fontColor
															.trim())) {
												tdStyle.append(" color:"
														+ fontColor + "; ");
											}
											tdStyle.append(" font-weight:"
													+ boldWeight + "; ");
											tdStyle.append(" font-size: "
													+ fontHeight + "%;");
											lsb.append(tdStyle + "\"");

											int width = (int) (sheet
													.getColumnWidth(cellNum) / 35.7); //
											int cellReginCol = getMergerCellRegionCol(
													sheet, rowNum, cellNum); // �ϲ����У�solspan��
											int cellReginRow = getMergerCellRegionRow(
													sheet, rowNum, cellNum);// �ϲ����У�rowspan��
											String align = convertAlignToHtml(cellStyle
													.getAlignment()); //
											String vAlign = convertVerticalAlignToHtml(cellStyle
													.getVerticalAlignment());

											lsb.append(" align=\"" + align
													+ "\" valign=\"" + vAlign
													+ "\" width=\"" + width
													+ "\" ");
											lsb.append(" colspan=\""
													+ cellReginCol
													+ "\" rowspan=\""
													+ cellReginRow + "\"");
											lsb.append(">" + getCellValue(cell)
													+ "</td>");
										}
									}
								}
								lsb.append("</tr>");
							}
						}
						lsb.append("</thead>");

						lsb.append("<tbody>");
						for (int rowNum = firstRowNum + 2; rowNum <= lastRowNum; rowNum++) {
							if (sheet.getRow(rowNum) != null) {// ����в�Ϊ�գ�
								HSSFRow row = sheet.getRow(rowNum);
								short firstCellNum = row.getFirstCellNum(); // ���еĵ�һ����Ԫ��
								short lastCellNum = row.getLastCellNum(); // ���е����һ����Ԫ��
								int height = (int) (row.getHeight() / 15.625); // �еĸ߶�
								lsb.append("<tr height=\""
										+ height
										+ "\" style=\"border:1px solid #000;border-width:0 1px 1px 0;margin:2px 0 2px 0;\">");
								for (short cellNum = firstCellNum; cellNum <= lastCellNum; cellNum++) { // ѭ�����е�ÿһ����Ԫ��
									HSSFCell cell = row.getCell(cellNum);
									if (cell != null) {
										if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
											continue;
										} else {
											StringBuffer tdStyle = new StringBuffer(
													"<td style=\"border:1px solid #000; border-width:0 1px 1px 0;margin:2px 0 2px 0; ");
											HSSFCellStyle cellStyle = cell
													.getCellStyle();
											HSSFPalette palette = workbook
													.getCustomPalette(); // ��HSSFPalette��������ɫ�Ĺ��ʱ�׼��ʽ
											HSSFColor hColor = palette
													.getColor(cellStyle
															.getFillForegroundColor());
											HSSFColor hColor2 = palette
													.getColor(cellStyle
															.getFont(workbook)
															.getColor());

											String bgColor = convertToStardColor(hColor);// ������ɫ
											short boldWeight = cellStyle
													.getFont(workbook)
													.getBoldweight(); // �����ϸ
											short fontHeight = (short) (cellStyle
													.getFont(workbook)
													.getFontHeight() / 2); // �����С
											String fontColor = convertToStardColor(hColor2); // ������ɫ
											if (bgColor != null
													&& !"".equals(bgColor
															.trim())) {
												tdStyle.append(" background-color:"
														+ bgColor + "; ");
											}
											if (fontColor != null
													&& !"".equals(fontColor
															.trim())) {
												tdStyle.append(" color:"
														+ fontColor + "; ");
											}
											tdStyle.append(" font-weight:"
													+ boldWeight + "; ");
											tdStyle.append(" font-size: "
													+ fontHeight + "%;");
											lsb.append(tdStyle + "\"");

											int width = (int) (sheet
													.getColumnWidth(cellNum) / 35.7); //
											int cellReginCol = getMergerCellRegionCol(
													sheet, rowNum, cellNum); // �ϲ����У�solspan��
											int cellReginRow = getMergerCellRegionRow(
													sheet, rowNum, cellNum);// �ϲ����У�rowspan��
											String align = convertAlignToHtml(cellStyle
													.getAlignment()); //
											String vAlign = convertVerticalAlignToHtml(cellStyle
													.getVerticalAlignment());

											lsb.append(" align=\"" + align
													+ "\" valign=\"" + vAlign
													+ "\" width=\"" + width
													+ "\" ");
											lsb.append(" colspan=\""
													+ cellReginCol
													+ "\" rowspan=\""
													+ cellReginRow + "\"");
											lsb.append(">" + getCellValue(cell)
													+ "</td>");
										}
									}
								}
								lsb.append("</tr>");
							}
						}
						lsb.append("</tbody>");
						lsb.append("</table></div>");
					}
				}
			}
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}
		lsb.append("</body></html>");
			
		String fileName2="C:\\Users\\mathide\\git\\QuotaManageSystem\\QuotaManageSystem\\src\\main\\webapp\\ff.html";
		FileOutputStream fo=new FileOutputStream(fileName2);
		fo.write(lsb.toString().getBytes());
		fo.close();
		
		return lsb;
	}
	
	

	/**
	 * ȡ�õ�Ԫ���ֵ
	 * 
	 * @param cell
	 * @return
	 * @throws IOException
	 */
	private static Object getCellValue(HSSFCell cell) throws IOException {
		Object value = "";
		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			value = cell.getRichStringCellValue().toString();
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				value = sdf.format(date);
			} else {
				double value_temp = (double) cell.getNumericCellValue();
				BigDecimal bd = new BigDecimal(value_temp);
				BigDecimal bd1 = bd.setScale(3, bd.ROUND_HALF_UP);
				value = bd1.doubleValue();

				/*
				 * DecimalFormat format = new DecimalFormat("#0.###"); value =
				 * format.format(cell.getNumericCellValue());
				 */
			}
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
			value = "";
		}
		return value;
	}

	/**
	 * �жϵ�Ԫ���ڲ��ںϲ���Ԫ��Χ�ڣ�����ǣ���ȡ��ϲ���������
	 * 
	 * @param sheet
	 *            ������
	 * @param cellRow
	 *            ���жϵĵ�Ԫ����к�
	 * @param cellCol
	 *            ���жϵĵ�Ԫ����к�
	 * @return
	 * @throws IOException
	 */
	private static int getMergerCellRegionCol(HSSFSheet sheet, int cellRow,
			int cellCol) throws IOException {
		int retVal = 0;
		int sheetMergerCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergerCount; i++) {
			CellRangeAddress cra = (CellRangeAddress) sheet.getMergedRegion(i);
			int firstRow = cra.getFirstRow(); // �ϲ���Ԫ��CELL��ʼ��
			int firstCol = cra.getFirstColumn(); // �ϲ���Ԫ��CELL��ʼ��
			int lastRow = cra.getLastRow(); // �ϲ���Ԫ��CELL������
			int lastCol = cra.getLastColumn(); // �ϲ���Ԫ��CELL������
			if (cellRow >= firstRow && cellRow <= lastRow) { // �жϸõ�Ԫ���Ƿ����ںϲ���Ԫ����
				if (cellCol >= firstCol && cellCol <= lastCol) {
					retVal = lastCol - firstCol + 1; // �õ��ϲ�������
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * �жϵ�Ԫ���Ƿ��Ǻϲ��ĵ�������ǣ���ȡ��ϲ���������
	 * 
	 * @param sheet
	 *            ����
	 * @param cellRow
	 *            ���жϵĵ�Ԫ����к�
	 * @param cellCol
	 *            ���жϵĵ�Ԫ����к�
	 * @return
	 * @throws IOException
	 */
	private static int getMergerCellRegionRow(HSSFSheet sheet, int cellRow,
			int cellCol) throws IOException {
		int retVal = 0;
		int sheetMergerCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergerCount; i++) {
			CellRangeAddress cra = (CellRangeAddress) sheet.getMergedRegion(i);
			int firstRow = cra.getFirstRow(); // �ϲ���Ԫ��CELL��ʼ��
			int firstCol = cra.getFirstColumn(); // �ϲ���Ԫ��CELL��ʼ��
			int lastRow = cra.getLastRow(); // �ϲ���Ԫ��CELL������
			int lastCol = cra.getLastColumn(); // �ϲ���Ԫ��CELL������
			if (cellRow >= firstRow && cellRow <= lastRow) { // �жϸõ�Ԫ���Ƿ����ںϲ���Ԫ����
				if (cellCol >= firstCol && cellCol <= lastCol) {
					retVal = lastRow - firstRow + 1; // �õ��ϲ�������
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * ��Ԫ�񱳾�ɫת��
	 * 
	 * @param hc
	 * @return
	 */
	private String convertToStardColor(HSSFColor hc) {
		StringBuffer sb = new StringBuffer("");
		if (hc != null) {
			int a = HSSFColor.AUTOMATIC.index;
			int b = hc.getIndex();
			if (a == b) {
				return null;
			}
			sb.append("#");
			for (int i = 0; i < hc.getTriplet().length; i++) {
				String str;
				String str_tmp = Integer.toHexString(hc.getTriplet()[i]);
				if (str_tmp != null && str_tmp.length() < 2) {
					str = "0" + str_tmp;
				} else {
					str = str_tmp;
				}
				sb.append(str);
			}
		}
		return sb.toString();
	}

	/**
	 * ��Ԫ��Сƽ����
	 * 
	 * @param alignment
	 * @return
	 */
	private String convertAlignToHtml(short alignment) {
		String align = "left";
		switch (alignment) {
		case HSSFCellStyle.ALIGN_LEFT:
			align = "left";
			break;
		case HSSFCellStyle.ALIGN_CENTER:
			align = "center";
			break;
		case HSSFCellStyle.ALIGN_RIGHT:
			align = "right";
			break;
		default:
			break;
		}
		return align;
	}

	/**
	 * ��Ԫ��ֱ����
	 * 
	 * @param verticalAlignment
	 * @return
	 */
	private String convertVerticalAlignToHtml(short verticalAlignment) {
		String valign = "middle";
		switch (verticalAlignment) {
		case HSSFCellStyle.VERTICAL_BOTTOM:
			valign = "bottom";
			break;
		case HSSFCellStyle.VERTICAL_CENTER:
			valign = "center";
			break;
		case HSSFCellStyle.VERTICAL_TOP:
			valign = "top";
			break;
		default:
			break;
		}
		return valign;
	}

}