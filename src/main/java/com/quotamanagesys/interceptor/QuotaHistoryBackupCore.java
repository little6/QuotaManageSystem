package com.quotamanagesys.interceptor;

import java.util.Calendar;
import java.util.Collection;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.Expose;
import com.quotamanagesys.dao.HistoryValueInsertSQLDao;
import com.quotamanagesys.dao.QuotaHistoryBackupDao;
import com.quotamanagesys.dao.QuotaItemDao;
import com.quotamanagesys.model.HistoryValueInsertSQL;
import com.quotamanagesys.model.QuotaHistoryBackUp;
import com.quotamanagesys.model.QuotaItem;

@Component
public class QuotaHistoryBackupCore extends HibernateDao{
	
	@Resource
	ResultTableCreator resultTableCreator;
	@Resource
	QuotaHistoryBackupDao quotaHistoryBackupDao;
	@Resource
	HistoryValueInsertSQLDao historyValueInsertSQLDao;
	@Resource
	QuotaItemDao quotaItemDao;
	
	//����ָ����Ϣ���ݼ�¼
	@Expose
	public void createQuotaHistoryBackup(){
		Session session=this.getSessionFactory().openSession();
		try {
			QuotaHistoryBackUp quotaHistoryBackUp=new QuotaHistoryBackUp();
			Calendar calendar=Calendar.getInstance();
			
			//��ȡִ��ʱ������
			int year=calendar.get(Calendar.YEAR);
			int month=calendar.get(Calendar.MONTH);
			
			//�����2�¿�ʼ���ſ�ִ����һ���ָ����Ϣ���ݲ���
			if (month>1){
				Collection<QuotaItem> quotaItems=quotaItemDao.getQuotaItemsByYear(year-1);
				//�����һ���ָ�궼����գ����޷�ִ����һ���ָ����Ϣ���ݲ���
				if (quotaItems.size()>0) {
					//���������ǰ�����ı��ݣ�����ɾ�������������°汾����
					deleteQuotaHistoryBackup(year-1);
					
					//ָ����Ϣ���ݰ�������Ҫ����Ϊ������䡢������������
					String tableName="QUOTA_ITEM_VIEW_"+(year-1);
					String tableCreateSQL=resultTableCreator.getInitTableString(tableName);
					
					quotaHistoryBackUp.setYear(year-1);
					quotaHistoryBackUp.setName((year-1)+"��ָ����Ϣ");
					quotaHistoryBackUp.setTableName(tableName);
					quotaHistoryBackUp.setTableCreateSQL(tableCreateSQL);
					session.save(quotaHistoryBackUp);
					session.flush();
					session.clear();
					
					for (QuotaItem quotaItem : quotaItems) {
						String valueInsertSQL=resultTableCreator.getInsertQuotaItemValuesIntoTableString(quotaItem,tableName);
						HistoryValueInsertSQL historyValueInsertSQL=new HistoryValueInsertSQL();
						historyValueInsertSQL.setQuotaHistoryBackUp(quotaHistoryBackUp);
						historyValueInsertSQL.setValueInsertSQL(valueInsertSQL);
						session.merge(historyValueInsertSQL);
						session.flush();
						session.clear();
					}
				}else {
					System.out.print("�޷��ҵ����ɱ��ݵ�����");
				}
			}else {
				System.out.print("��ǰ���ڲ�������ָ����Ϣ����");
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}

	//ɾ��ָ�����ָ�걸�ݼ�¼
	@Expose
	public void deleteQuotaHistoryBackup(int year){
		Session session=this.getSessionFactory().openSession();
		try {
			QuotaHistoryBackUp quotaHistoryBackUp=quotaHistoryBackupDao.getQuotaHistoryBackUpByYear(year);
			Collection<HistoryValueInsertSQL> historyValueInsertSQLs=historyValueInsertSQLDao.getHistoryValueInsertSQLsByHistoryBackup(quotaHistoryBackUp.getId());
			for (HistoryValueInsertSQL historyValueInsertSQL : historyValueInsertSQLs) {
				historyValueInsertSQL.setQuotaHistoryBackUp(null);
				session.delete(historyValueInsertSQL);
				session.flush();
				session.clear();
			}
			session.delete(quotaHistoryBackUp);
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
	
	//����ָ�걸�ݼ�¼���ɸü�¼��ȶ�Ӧ��ָ����Ϣ���ݱ�
	@Expose
	public void createQuotaHistoryBackupTable(int year){
		QuotaHistoryBackUp quotaHistoryBackUp=quotaHistoryBackupDao.getQuotaHistoryBackUpByYear(year);
		if (!quotaHistoryBackUp.equals(null)) {
			excuteSQL("DROP TABLE IF EXISTS "+quotaHistoryBackUp.getTableName());
			excuteSQL(quotaHistoryBackUp.getTableCreateSQL());
			Collection<HistoryValueInsertSQL> historyValueInsertSQLs=historyValueInsertSQLDao.getHistoryValueInsertSQLsByHistoryBackup(quotaHistoryBackUp.getId());
			
			for (HistoryValueInsertSQL historyValueInsertSQL : historyValueInsertSQLs) {
				excuteSQL(historyValueInsertSQL.getValueInsertSQL());
			}
		}
	}
	
	//��������Ѿ����ɵ�ָ����Ϣ���ݱ��ͷ����ݿ�洢�ռ�
	@Expose
	public void clearQuotaHistoryBackupTables(){
		Collection<QuotaHistoryBackUp> quotaHistoryBackUps=quotaHistoryBackupDao.getAll();
		for (QuotaHistoryBackUp quotaHistoryBackUp : quotaHistoryBackUps) {
			excuteSQL("DROP TABLE IF EXISTS "+quotaHistoryBackUp.getTableName());
		}
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
}
