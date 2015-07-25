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
import com.quotamanagesys.model.QuotaItem;

@Component
public class PushValueToPmis extends HibernateDao{

	@Resource
	QuotaItemDao quotaItemDao;
	
	@Expose
	public void pushFinishValue() throws SQLException{
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
				String updateString="";
				
				QuotaItem quotaItem=quotaItemDao.getQuotaItemByNameAndYearAndMonthAndCover(name, year, month, coverName);
				if (quotaItem!=null) {
					if (quotaItem.getFinishValue()==null) {
						updateString="UPDATE push_value_to_pmis SET 完成值=NULL,导入情况监控='完成值为空，请核查指标完成值填写情况'"
								+" WHERE 指标名称='"+rs.getString("指标名称")+"'"
								+" and 指标口径='"+rs.getString("指标口径")+"'"
								+" and 考核年度='"+rs.getString("考核年度")+"'"
								+" and 考核月度='"+rs.getString("考核月度")+"'";
					}else {
						updateString="UPDATE push_value_to_pmis SET 完成值='"+quotaItem.getFinishValue()+"',导入情况监控='正常'"
								+" WHERE 指标名称='"+rs.getString("指标名称")+"'"
								+" and 指标口径='"+rs.getString("指标口径")+"'"
								+" and 考核年度='"+rs.getString("考核年度")+"'"
								+" and 考核月度='"+rs.getString("考核月度")+"'";
					}	 
				}else {
					updateString="UPDATE push_value_to_pmis SET 导入情况监控='无匹配指标，请核查'"
							+" WHERE 指标名称='"+rs.getString("指标名称")+"'"
							+" and 指标口径='"+rs.getString("指标口径")+"'"
							+" and 考核年度='"+rs.getString("考核年度")+"'"
							+" and 考核月度='"+rs.getString("考核月度")+"'";
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
	
	@Expose
	public void pushStandardValue(){
		
	}
	
	@Expose
	public void pushWeight(){
		
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