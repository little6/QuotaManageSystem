package com.quotamanagesys.interceptor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.Expose;
import com.quotamanagesys.dao.QuotaItemViewTableManageDao;
import com.quotamanagesys.model.QuotaItemViewTableManage;

@Component
public class WriteExcel extends HibernateDao{
	
	@Resource
	QuotaItemViewTableManageDao quotaItemViewTableManageDao;
	
	@Expose
	public void writeExcel(int year,int month) throws IOException, SQLException{
		String fileName="C:\\DC_\\河池供电局X年X月关键业绩考核指标完成情况表.xls";
		InputStream fileInputStream=new FileInputStream(fileName);
		HSSFWorkbook workbook=new HSSFWorkbook(fileInputStream);
		
		HSSFSheet sheet=workbook.getSheetAt(0);
		if (sheet==null) {
			System.out.println("sheet is null");
		}else {
			boolean isSuccess=true;
			try {
				QuotaItemViewTableManage quotaItemViewTableManage=quotaItemViewTableManageDao.getItemViewTableManageByYear(year);
				if (quotaItemViewTableManage!=null) {
					HSSFRow title=sheet.getRow(0);
					title.getCell(0).setCellValue("河池供电局"+year+"年"+month+"月关键业绩考核指标完成情况表");

					for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
						HSSFRow row=sheet.getRow(rowNum);
						if (row==null) {
							System.out.println("row is null");
						} else {
							String propertyName=sheet.getRow(rowNum).getCell(3).toString();
							String quotaName=row.getCell(2).getStringCellValue();
							String quotaCover=row.getCell(4).getStringCellValue();
							String sqlString="select * from "+quotaItemViewTableManage.getTableName()+" where 月度="+month
									+" and 指标名称='"+quotaName+"' and 口径='"+quotaCover+"'";
							
							List resultList=getQueryResults(sqlString);
							if (resultList.size()>0) {
								Object result=resultList.get(0);
								Map map=(Map) result;
								
								if (map.get("计量单位")==null||map.get("计量单位").equals("#")) {
									row.getCell(5).setCellValue(" ");
								} else {
									String jldw=map.get("计量单位").toString();
									row.getCell(5).setCellValue(jldw);
								}
								
								if (map.get(propertyName)==null||map.get(propertyName).equals("#")) {
									row.getCell(6).setCellValue(" ");
								} else {
									String gskh=map.get(propertyName).toString();
									row.getCell(6).setCellValue(gskh);
								}
								
								if (map.get("完成值")==null||map.get("完成值").equals("#")) {
									row.getCell(7).setCellValue(" ");
								} else {
									String wcz=map.get("完成值").toString();
									row.getCell(7).setCellValue(wcz);
								}
								
								if (map.get("同比变化")==null||map.get("同比变化").equals("#")) {
									row.getCell(8).setCellValue(" ");
								} else {
									String tb=map.get("同比变化").toString();
									row.getCell(8).setCellValue(tb);
								}
								
								if (map.get("累计值")==null||map.get("累计值").equals("#")) {
									row.getCell(9).setCellValue(" ");
								} else {
									String lj=map.get("累计值").toString();
									row.getCell(9).setCellValue(lj);
								}
								
								if (map.get("累计同比变化")==null||map.get("累计同比变化").equals("#")) {
									row.getCell(10).setCellValue(" ");
								} else {
									String ljtb=map.get("累计同比变化").toString();
									row.getCell(10).setCellValue(ljtb);
								}
								
								if (map.get(propertyName+"月度监控")==null||map.get(propertyName+"月度监控").equals("#")) {
									row.getCell(11).setCellValue(" ");
								} else {
									String gskhjk=map.get(propertyName+"月度监控").toString();
									if (gskhjk.equals("0.0")) {
										HSSFFont font=row.getCell(11).getCellStyle().getFont(workbook);
										HSSFPalette palette=workbook.getCustomPalette();
										HSSFColor color=palette.findSimilarColor(255, 0, 0);
										HSSFCellStyle cellStyle=row.getCell(11).getCellStyle();
										font.setColor(color.getIndex());
										cellStyle.setFont(font);
										row.getCell(11).setCellStyle(cellStyle);
										row.getCell(11).setCellValue("█");
									} else if (gskhjk.equals("1.0")) {
										HSSFFont font=row.getCell(11).getCellStyle().getFont(workbook);
										HSSFPalette palette=workbook.getCustomPalette();
										HSSFColor color=palette.findSimilarColor(0, 255, 0);
										HSSFCellStyle cellStyle=row.getCell(11).getCellStyle();
										font.setColor(color.getIndex());
										cellStyle.setFont(font);
										row.getCell(11).setCellStyle(cellStyle);
										row.getCell(11).setCellValue("█");
									}
								}
							}else {
								row.getCell(5).setCellValue(" ");
								row.getCell(6).setCellValue(" ");
								row.getCell(7).setCellValue(" ");
								row.getCell(8).setCellValue(" ");
								row.getCell(9).setCellValue(" ");
								row.getCell(10).setCellValue(" ");
								row.getCell(11).setCellValue(" ");
							}
						}
					}
				}else {
					isSuccess=false;
					System.out.print("无该年度数据："+year);
				}
			} catch (Exception e) {
				isSuccess=false;
			}
		}
		
		String fileName2="C:\\DC_\\河池供电局X年X月关键业绩考核指标完成情况表.xls";
		//String fileName2="C:\\DC_\\河池供电局"+year+"年"+month+"月关键业绩考核指标完成情况表.xlsx";
		fileName2=URLEncoder.encode(fileName2, "UTF-8");
		FileOutputStream fileOutputStream=new FileOutputStream(fileName);
		workbook.write(fileOutputStream);
		fileOutputStream.flush();
		fileOutputStream.close();
	}

	public ResultSet getResultSet(Connection conn,String sql) throws SQLException{
		PreparedStatement statement=conn.prepareStatement(sql);
		ResultSet rs=statement.executeQuery();
		return rs;
	}
	
	public List getQueryResults(String SQL) {
		Session session=this.getSessionFactory().openSession();
		List resultList = null;
		try {
			resultList=session.createSQLQuery(SQL).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			session.close();
		}
		return resultList;
	}

	public Connection getDBConnection(){
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/quotamanagesysdb?useUnicode=true&amp;characterEncoding=UTF-8";
		String user = "root"; 
		String password = "abcd1234";
		try { 
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, user, password);
			return conn;
	     }catch(Exception e){
	    	System.out.print(e.toString());
	    	return null;
	     }
	}
}
