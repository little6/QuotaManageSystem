package com.quotamanagesys.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.Expose;
import com.quotamanagesys.dao.QuotaItemDao;
import com.quotamanagesys.dao.QuotaPropertyValueDao;
import com.quotamanagesys.model.QuotaItem;
import com.quotamanagesys.model.QuotaPropertyValue;

@Component
public class PushValueToPmis extends HibernateDao{

	@Resource
	QuotaItemDao quotaItemDao;
	@Resource
	QuotaPropertyValueDao quotaPropertyValueDao;
	
	@Expose
	public void pushValues() throws SQLException{
		Connection conn=getDBConnection();
		ResultSet rs=null;
		boolean isSuccess=true;
		
		try {
			rs=getResultSet(conn,"select * from push_value_to_pmis");
		} catch (Exception e) {
			isSuccess=false;
		}
		
		if (isSuccess) {
			while (rs.next()) {
				String name=rs.getString("指标名称");
				String coverName=rs.getString("指标口径");
				int year=Integer.parseInt(rs.getString("考核年度"));
				int month=Integer.parseInt(rs.getString("考核月度"));
				String propertyName=rs.getString("指标属性");
				String updateString="";
				
				QuotaItem quotaItem=quotaItemDao.getQuotaItemByNameAndYearAndMonthAndCover(name, year, month, coverName);	
				
				String whereString=" WHERE 指标名称='"+rs.getString("指标名称")+"'"
						+" and 指标口径='"+rs.getString("指标口径")+"'"
						+" and 考核年度='"+rs.getString("考核年度")+"'"
						+" and 考核月度='"+rs.getString("考核月度")+"'";
				
				String importStatusString="";
				String finishValueString="";
				String quotaPropertyValueString="";
				
				if (quotaItem!=null) {
					QuotaPropertyValue quotaPropertyValue=quotaPropertyValueDao.getQuotaPropertyValueByQuotaItemAndPropertyName(quotaItem.getId(),propertyName);
					if (quotaPropertyValue==null) {
						if (quotaItem.getFinishValue()==null) {
							importStatusString="'1、完成值为空，请核查指标完成值填写情况   2、年度目标值不存在，请检查指标属性设置'";
							finishValueString="NULL";
							quotaPropertyValueString="NULL";
							
						}else {
							importStatusString="'1、年度目标值不存在，请检查指标属性设置'";
							finishValueString="'"+quotaItem.getFinishValue()+"'";
							quotaPropertyValueString="NULL";
						}	 
					} else {
						if (quotaItem.getFinishValue()==null) {
							importStatusString="'1、完成值为空，请核查指标完成值填写情况'";
							finishValueString="NULL";
							quotaPropertyValueString="'"+quotaPropertyValue.getValue()+"'";
							
						}else {
							importStatusString="'正常'";
							finishValueString="'"+quotaItem.getFinishValue()+"'";
							quotaPropertyValueString="'"+quotaPropertyValue.getValue()+"'";
						}	 
					}

					updateString="UPDATE push_value_to_pmis SET 完成值="+finishValueString+",年度目标值="+quotaPropertyValueString+",导入情况监控="+importStatusString
							+whereString;
				}else {
					importStatusString="无匹配指标，请核查";
					updateString="UPDATE push_value_to_pmis SET 导入情况监控='"+importStatusString+"'"
							+whereString;
				}
				
				try {
					excuteSQL(updateString);
				} catch (Exception e) {
					System.out.println(e.toString());
				}
			}
		}
		rs.close();
		conn.close();
	}
	
	public void excuteSQL(String SQL) {
		Session session = this.getSessionFactory().openSession();
		try {
			session.createSQLQuery(SQL).executeUpdate();
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}
	
	public ResultSet getResultSet(Connection conn,String sql) throws SQLException{
		Statement statement=conn.createStatement();
		ResultSet rs=statement.executeQuery(sql);
		return rs;
	}
	
	public Connection getDBConnection(){
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/quotamanagesysdb?useUnicode=true&amp;characterEncoding=UTF-8";
		String user = "root"; 
		String password = "scmis@*08";
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