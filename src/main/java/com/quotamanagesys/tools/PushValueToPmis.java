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
				String name=rs.getString("ָ������");
				String coverName=rs.getString("ָ��ھ�");
				int year=Integer.parseInt(rs.getString("�������"));
				int month=Integer.parseInt(rs.getString("�����¶�"));
				String propertyName=rs.getString("ָ������");
				String updateString="";
				
				QuotaItem quotaItem=quotaItemDao.getQuotaItemByNameAndYearAndMonthAndCover(name, year, month, coverName);	
				
				String whereString=" WHERE ָ������='"+rs.getString("ָ������")+"'"
						+" and ָ��ھ�='"+rs.getString("ָ��ھ�")+"'"
						+" and �������='"+rs.getString("�������")+"'"
						+" and �����¶�='"+rs.getString("�����¶�")+"'";
				
				String importStatusString="";
				String finishValueString="";
				String quotaPropertyValueString="";
				
				if (quotaItem!=null) {
					QuotaPropertyValue quotaPropertyValue=quotaPropertyValueDao.getQuotaPropertyValueByQuotaItemAndPropertyName(quotaItem.getId(),propertyName);
					if (quotaPropertyValue==null) {
						if (quotaItem.getFinishValue()==null) {
							importStatusString="'1�����ֵΪ�գ���˲�ָ�����ֵ��д���   2�����Ŀ��ֵ�����ڣ�����ָ����������'";
							finishValueString="NULL";
							quotaPropertyValueString="NULL";
							
						}else {
							importStatusString="'1�����Ŀ��ֵ�����ڣ�����ָ����������'";
							finishValueString="'"+quotaItem.getFinishValue()+"'";
							quotaPropertyValueString="NULL";
						}	 
					} else {
						if (quotaItem.getFinishValue()==null) {
							importStatusString="'1�����ֵΪ�գ���˲�ָ�����ֵ��д���'";
							finishValueString="NULL";
							quotaPropertyValueString="'"+quotaPropertyValue.getValue()+"'";
							
						}else {
							importStatusString="'����'";
							finishValueString="'"+quotaItem.getFinishValue()+"'";
							quotaPropertyValueString="'"+quotaPropertyValue.getValue()+"'";
						}	 
					}

					updateString="UPDATE push_value_to_pmis SET ���ֵ="+finishValueString+",���Ŀ��ֵ="+quotaPropertyValueString+",����������="+importStatusString
							+whereString;
				}else {
					importStatusString="��ƥ��ָ�꣬��˲�";
					updateString="UPDATE push_value_to_pmis SET ����������='"+importStatusString+"'"
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