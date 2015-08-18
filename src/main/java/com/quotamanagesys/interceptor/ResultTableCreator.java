package com.quotamanagesys.interceptor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.Expose;
import com.quotamanagesys.dao.QuotaFormulaResultDao;
import com.quotamanagesys.dao.QuotaFormulaResultValueDao;
import com.quotamanagesys.dao.QuotaItemDao;
import com.quotamanagesys.dao.QuotaItemViewTableManageDao;
import com.quotamanagesys.dao.QuotaPropertyDao;
import com.quotamanagesys.dao.QuotaPropertyValueDao;
import com.quotamanagesys.dao.QuotaTargetValueDao;
import com.quotamanagesys.model.QuotaFormulaResult;
import com.quotamanagesys.model.QuotaFormulaResultValue;
import com.quotamanagesys.model.QuotaItem;
import com.quotamanagesys.model.QuotaItemCreator;
import com.quotamanagesys.model.QuotaProperty;
import com.quotamanagesys.model.QuotaPropertyValue;
import com.quotamanagesys.model.QuotaTargetValue;
import com.quotamanagesys.model.QuotaType;

@Component
public class ResultTableCreator extends HibernateDao{

	@Resource
	QuotaPropertyDao quotaPropertyDao;
	@Resource
	QuotaFormulaResultDao quotaFormulaResultDao;
	@Resource
	QuotaFormulaResultValueDao quotaFormulaResultValueDao;
	@Resource
	QuotaPropertyValueDao quotaPropertyValueDao;
	@Resource
	QuotaItemDao quotaItemDao;
	@Resource
	QuotaTargetValueDao quotaTargetValueDao;
	@Resource
	QuotaItemViewTableManageDao quotaItemViewTableManageDao;
	
	//����ȫ��ָ������
	@Expose
	public void createOrUpdateAll() throws SQLException{
		Collection<QuotaItem> quotaItems=quotaItemDao.getAll();
		createOrUpdateResultTable(quotaItems);
	}
	
	//����ָ�꼯�϶�Ӧ������
	@Expose
	public void createOrUpdateResultTable(Collection<QuotaItem> quotaItems) throws SQLException{	
		Connection conn=getDBConnection();
		ResultSet rs=null;
		boolean isSuccess=true;

		Calendar calendar=Calendar.getInstance();
		
		//��ȡִ��ʱ������
		int year=calendar.get(Calendar.YEAR);
		int month=calendar.get(Calendar.MONTH)+1;//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
		
		String tableName="quota_item_view_";
		if (month>1) {
			tableName=tableName+year;
			quotaItemViewTableManageDao.initQuotaItemViewTableManage(year);
		}else {
			tableName=tableName+(year-1);
			quotaItemViewTableManageDao.initQuotaItemViewTableManage(year-1);
		}
		
		try {
			rs=getResultSet(conn,"select * from "+tableName);
		} catch (Exception e) {
			isSuccess=false;
		}
		
		if (isSuccess) {
			ArrayList<String> quotaItemIds=new ArrayList<String>();
			while (rs.next()) {
				String quotaItemId=rs.getString("ָ��id");
				if (quotaItemId!=null) {
					quotaItemIds.add(quotaItemId);
				}else {
					System.out.println("quotaId is null"+'\n');
				}		
			}
			
			for (QuotaItem quotaItem : quotaItems) {
				for (String quotaItemId : quotaItemIds) {
					if ((quotaItem.getId()).equals(quotaItemId)) {
						Collection<QuotaItem> deleteItems=new ArrayList<QuotaItem>();
						deleteItems.add(quotaItem);
						deleteItemsFromResultTable(deleteItems);
						quotaItemIds.remove(quotaItemId);
						break;
					}else {
						continue;
					}
				}

				String insertSqlString=getInsertQuotaItemValuesIntoTableString(quotaItem,tableName);
				if (insertSqlString!=null) {
					try {
						excuteSQL(insertSqlString);
					} catch (Exception e) {
						System.out.println(e.toString());
					}
				} else {
					System.out.println(quotaItem.getId()+'\n');
				}
			}	
		}else {
			excuteSQL(getInitTableString(tableName));
			for (QuotaItem quotaItem : quotaItems) {
				String insertSqlString=getInsertQuotaItemValuesIntoTableString(quotaItem,tableName);
				if (insertSqlString!=null) {
					excuteSQL(insertSqlString);
				} else {
					System.out.println(quotaItem.getId()+'\n');
				}
			}
		}
		conn.close();
	}
	
	@Expose
	public void deleteItemsFromResultTable(Collection<QuotaItem> quotaItems) throws SQLException{
		Connection conn=getDBConnection();
		ResultSet rs=null;
		boolean isSuccess=true;

		Calendar calendar=Calendar.getInstance();
		
		//��ȡִ��ʱ������
		int year=calendar.get(Calendar.YEAR);
		int month=calendar.get(Calendar.MONTH)+1;//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
		
		String tableName="quota_item_view_";
		if (month>1) {
			tableName=tableName+year;
		}else {
			tableName=tableName+(year-1);
		}
		
		try {
			rs=getResultSet(conn,"select * from "+tableName);
		} catch (Exception e) {
			isSuccess=false;
		}
		
		if (isSuccess) {
			for (QuotaItem quotaItem : quotaItems) {
				String deleteString="DELETE FROM "+tableName+" WHERE ָ��id='"+quotaItem.getId()+"'";
				excuteSQL(deleteString);
			}	
		}else {
			System.out.println("���ݱ����ڣ�����ִ��ɾ������");
		}
		conn.close();
	}
	
	public String getInsertQuotaItemValuesIntoTableString(QuotaItem quotaItem,String tableName){
		try {
			QuotaItemCreator quotaItemCreator=quotaItem.getQuotaItemCreator();
			QuotaType quotaType=quotaItemCreator.getQuotaType();
			String sqlString="INSERT INTO "+tableName+"(";
			String staticColumnsString;
			String staticValuesString;
			staticColumnsString="ָ��id,"
					+"ָ������,"
					+"���,"
					+ "�¶�,"
					+ "ָ��רҵ,"
					+ "ָ������id,"
					+ "ָ�꼶��,"
					+ "������λ,"
					+ "С��λ��,"
					+ "����Ƶ��,"
					+ "������id,"
					+ "������,"
					+ "���β���id,"
					+ "���β���,"
					+ "�ھ�id,"
					+ "�ھ�,"
					+ "ά��id,"
					+ "ά��,"
					+ "���ֵ,"
					+ "�ۼ�ֵ,"
					+ "ȥ��ͬ��ֵ,"
					+ "ȥ��ͬ���ۼ�ֵ,"
					+ "�����ʱ��,"
					+ "������ʱ��,"
					+ "���,"
					+ "�춯ԭ��,"
					+ "�ύ״̬,"
					+ "���ʱ";
			
			String professionName=null;
			if (quotaType.getQuotaProfession()==null) {
				professionName=null;
			}else {
				professionName=quotaType.getQuotaProfession().getName();
			}
			
			String firstSubmitTime="";
			if (quotaItem.getFirstSubmitTime()!=null) { 
				firstSubmitTime=(quotaItem.getFirstSubmitTime().getYear()+1900)+"��"
						+(quotaItem.getFirstSubmitTime().getMonth()+1)+"��"
						+quotaItem.getFirstSubmitTime().getDate()+"��";
			}
			String lastSubmitTime="";
			if (quotaItem.getLastSubmitTime()!=null) {
				lastSubmitTime=(quotaItem.getLastSubmitTime().getYear()+1900)+"��"
						+(quotaItem.getLastSubmitTime().getMonth()+1)+"��"
						+quotaItem.getLastSubmitTime().getDate()+"��";
			}
			String usernameOfLastSubmit="";
			if (quotaItem.getUsernameOfLastSubmit()!=null) {
				usernameOfLastSubmit=quotaItem.getUsernameOfLastSubmit();
			}
			String redLightReason="";
			if (quotaItem.getRedLightReason()!=null) {
				redLightReason=quotaItem.getRedLightReason();
			}
			
			staticValuesString="'"+quotaItem.getId()+"',"
					+"'"+quotaItemCreator.getName()+"',"
					+quotaItem.getYear()+","
					+quotaItem.getMonth()+","
					+"'"+professionName+"',"
					+"'"+quotaType.getId()+"',"
					+"'"+quotaType.getQuotaLevel().getName()+"',"
					+"'"+quotaType.getQuotaUnit().getName()+"',"
					+quotaType.getDigit()+","
					+"'"+quotaType.getRate()+"',"
					+"'"+quotaType.getManageDept().getId()+"',"
					+"'"+quotaType.getManageDept().getName()+"',"
					+"'"+quotaItemCreator.getQuotaDutyDept().getId()+"',"
					+"'"+quotaItemCreator.getQuotaDutyDept().getName()+"',"
					+"'"+quotaItemCreator.getQuotaCover().getId()+"',"
					+"'"+quotaItemCreator.getQuotaCover().getName()+"',"
					+"'"+quotaType.getQuotaDimension().getId()+"',"
					+"'"+quotaType.getQuotaDimension().getName()+"',";
					
			
			if (quotaItem.isAllowSubmit()==false) {
				staticValuesString=staticValuesString+"'','','','','','','','',"
						+quotaItem.isAllowSubmit()+","
						+quotaItem.isOverTime();
			}else {
				staticValuesString=staticValuesString+quotaItem.getFinishValue()+","
						+quotaItem.getAccumulateValue()+","
						+quotaItem.getSameTermValue()+","
						+quotaItem.getSameTermAccumulateValue()+","
						+"'"+firstSubmitTime+"',"
						+"'"+lastSubmitTime+"',"
						+"'"+usernameOfLastSubmit+"',"
						+"'"+redLightReason+"',"
						+quotaItem.isAllowSubmit()+","
						+quotaItem.isOverTime();
			}	
			
			String dynamicColumnsString="";
			String dynamicValuesString="";
			Collection<QuotaPropertyValue> quotaPropertyValues=quotaPropertyValueDao.getQuotaPropertyValuesByQuotaItemCreator(quotaItem.getQuotaItemCreator().getId());
			Collection<QuotaTargetValue> quotaTargetValues=quotaTargetValueDao.getQuotaTargetValuesByQuotaItem(quotaItem.getId());
			
			if (quotaPropertyValues.size()>0) {
				for (QuotaPropertyValue quotaPropertyValue : quotaPropertyValues) {
					if (dynamicColumnsString=="") {
						dynamicColumnsString=quotaPropertyValue.getQuotaProperty().getName();
						dynamicValuesString="'"+quotaPropertyValue.getValue()+"'";
					}else {
						dynamicColumnsString=dynamicColumnsString+","+quotaPropertyValue.getQuotaProperty().getName();
						dynamicValuesString=dynamicValuesString+","+"'"+quotaPropertyValue.getValue()+"'";
					}
				}
			}
			
			if (quotaTargetValues.size()>0) {
				for (QuotaTargetValue quotaTargetValue : quotaTargetValues) {
					if (dynamicColumnsString=="") {
						dynamicColumnsString=quotaTargetValue.getQuotaProperty().getName()+"_�¶�";
						dynamicValuesString="'"+quotaTargetValue.getValue()+"'";
					}else {
						dynamicColumnsString=dynamicColumnsString+","+quotaTargetValue.getQuotaProperty().getName()+"_�¶�";
						dynamicValuesString=dynamicValuesString+","+"'"+quotaTargetValue.getValue()+"'";
					}
				}
			}
			
			Collection<QuotaFormulaResultValue> quotaFormulaResultValues=quotaFormulaResultValueDao.getQuotaFormulaResultValuesByQuotaItem(quotaItem.getId());
			if (quotaFormulaResultValues.size()>0) {
				for (QuotaFormulaResultValue quotaFormulaResultValue : quotaFormulaResultValues) {
					if (dynamicColumnsString=="") {
						dynamicColumnsString=quotaFormulaResultValue.getQuotaFormulaResult().getName();
						if (quotaItem.isAllowSubmit()==false) {
							//δ�ύָ���������Ϊ#
							dynamicValuesString="'#'";
						} else {
							dynamicValuesString="'"+quotaFormulaResultValue.getValue()+"'";
						}	
					}else {
						dynamicColumnsString=dynamicColumnsString+","+quotaFormulaResultValue.getQuotaFormulaResult().getName();
						if (quotaItem.isAllowSubmit()==false) {
							//δ�ύָ���������Ϊ#
							dynamicValuesString=dynamicValuesString+",'#'";
						} else {
							dynamicValuesString=dynamicValuesString+","+"'"+quotaFormulaResultValue.getValue()+"'";
						}
					}
				}
			}
			
			if (dynamicColumnsString=="") {
				sqlString=sqlString+staticColumnsString+") VALUES("+staticValuesString+")";
			}else {
				sqlString=sqlString+staticColumnsString+","+dynamicColumnsString+") VALUES("+staticValuesString+","+dynamicValuesString+")";
			}
			return sqlString;
		} catch (Exception e) {
			System.out.println(quotaItem.getId()+"�׳��쳣Ϊ��"+e.toString()+'\n');
			return null;
		}
	}
	
	public String getInitTableString(String tableName){
		String sqlString="CREATE TABLE "+tableName+"(";
		String staticColumnsString="ָ��id VARCHAR(255),"
				+ "ָ������ VARCHAR(255),"
				+ "��� INT,"
				+ "�¶� INT,"
				+ "ָ��רҵ VARCHAR(255),"
				+ "ָ������id VARCHAR(255),"
				+ "ָ�꼶�� VARCHAR(255),"
				+ "������λ VARCHAR(255),"
				+ "С��λ�� INT,"
				+ "����Ƶ�� VARCHAR(255),"
				+ "������id VARCHAR(255),"
				+ "������ VARCHAR(255),"
				+ "���β���id VARCHAR(255),"
				+ "���β��� VARCHAR(255),"
				+ "�ھ�id VARCHAR(255),"
				+ "�ھ� VARCHAR(255),"
				+ "ά��id VARCHAR(255),"
				+ "ά�� VARCHAR(255),"
				+ "���ֵ VARCHAR(255),"
				+ "�ۼ�ֵ VARCHAR(255),"
				+ "ȥ��ͬ��ֵ VARCHAR(255),"
				+ "ȥ��ͬ���ۼ�ֵ VARCHAR(255),"
				+ "�����ʱ�� VARCHAR(255),"
				+ "������ʱ�� VARCHAR(255),"
				+ "��� VARCHAR(255),"
				+ "�춯ԭ�� VARCHAR(1500),"
				+ "�ύ״̬ BIT(1),"
				+ "���ʱ BIT(1)";
		String dynamicColumnsString="";
		Collection<QuotaProperty> quotaProperties=quotaPropertyDao.getAll();
		if (quotaProperties.size()>0) {
			for (QuotaProperty quotaProperty : quotaProperties) {
				if (dynamicColumnsString=="") {
					dynamicColumnsString=quotaProperty.getName()+" VARCHAR(255),"+quotaProperty.getName()+"_�¶�"+" VARCHAR(255)";
				}else {
					dynamicColumnsString=dynamicColumnsString+","+quotaProperty.getName()+" VARCHAR(255),"+quotaProperty.getName()+"_�¶�"+" VARCHAR(255)";
				}
			}
		}	
		Collection<QuotaFormulaResult> quotaFormulaResults=quotaFormulaResultDao.getAll();
		if (quotaFormulaResults.size()>0) {
			for (QuotaFormulaResult quotaFormulaResult : quotaFormulaResults) {
				if (dynamicColumnsString=="") {
					dynamicColumnsString=quotaFormulaResult.getName()+" VARCHAR(255)";
				}else {
					dynamicColumnsString=dynamicColumnsString+","+quotaFormulaResult.getName()+" VARCHAR(255)";
				}
			}
		}
		if (dynamicColumnsString=="") {
			sqlString=sqlString+staticColumnsString+dynamicColumnsString+"}";
		}else {
			sqlString=sqlString+staticColumnsString+","+dynamicColumnsString+")";
		}
		return sqlString;
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
	
	public ResultSet getResultSet(Connection conn,String sql) throws SQLException{
		PreparedStatement statement=conn.prepareStatement(sql);
		ResultSet rs=statement.executeQuery();
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
	    	System.out.println(e.toString());
	    	return null;
	     }
	}
}
