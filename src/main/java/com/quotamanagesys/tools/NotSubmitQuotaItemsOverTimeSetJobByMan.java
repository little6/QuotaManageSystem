package com.quotamanagesys.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.model.DefaultDept;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.Expose;
import com.quotamanagesys.dao.DepartmentDao;
import com.quotamanagesys.dao.QuotaItemDao;
import com.quotamanagesys.interceptor.ResultTableCreator;
import com.quotamanagesys.model.QuotaItem;

@Component
public class NotSubmitQuotaItemsOverTimeSetJobByMan extends HibernateDao{

	@Resource
	QuotaItemDao quotaItemDao;
	@Resource  
	DepartmentDao departmentDao;
	@Resource
	ResultTableCreator resultTableCreator;
	
	//����δ�ύ��ָ�꣬����ÿ��5�պ��Զ���Ϊ��ʱָ��
	@Expose
	public void execute(){	
		Collection<DefaultDept> depts=departmentDao.getAll();
		Collection<QuotaItem> notSubmitQuotaItems=new ArrayList<QuotaItem>();
		
		Calendar calendar=Calendar.getInstance();	
		//��ȡִ��ʱ������
		//����
		int year=calendar.get(Calendar.YEAR);
		//����
		int month=calendar.get(Calendar.MONTH);//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
	
		for (DefaultDept dept : depts) {
			//ȥ��12�����
			if (month==0) {
				Collection<QuotaItem> yearRateQuotaItemsOverTime=quotaItemDao.getQuotaItemsNotAllowSubmitByManageDept(dept.getId(), "��");
				Collection<QuotaItem> monthRateQuotaItemsOverTime=quotaItemDao.getQuotaItemsNotAllowSubmitByManageDept(dept.getId(), "��");
				if (yearRateQuotaItemsOverTime!=null) {
					notSubmitQuotaItems.addAll(yearRateQuotaItemsOverTime);
				}
				if (monthRateQuotaItemsOverTime!=null) {
					notSubmitQuotaItems.addAll(monthRateQuotaItemsOverTime);
				}
			}else {
				Collection<QuotaItem> monthRateQuotaItemsOverTime=quotaItemDao.getQuotaItemsNotAllowSubmitByManageDept(dept.getId(), "��");
				if (monthRateQuotaItemsOverTime!=null) {
					notSubmitQuotaItems.addAll(monthRateQuotaItemsOverTime);
				}
			}
		}
		
		if (notSubmitQuotaItems.size()>0) {
			Session session=this.getSessionFactory().openSession();
			try {
				for (QuotaItem quotaItem : notSubmitQuotaItems) {
					quotaItem.setOverTime(true);
					session.merge(quotaItem);
					session.flush();
				}
				resultTableCreator.createOrUpdateResultTable(notSubmitQuotaItems);
			} catch (Exception e) {
				System.out.print(e.toString());
			}finally{
				session.flush();
				session.close();
			}
		}
	}

}
