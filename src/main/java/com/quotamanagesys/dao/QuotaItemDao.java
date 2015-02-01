package com.quotamanagesys.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.core.exception.NoneLoginException;
import com.bstek.bdf2.core.model.DefaultUser;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Criterion;
import com.bstek.dorado.data.provider.Page;
import com.bstek.dorado.data.provider.filter.FilterCriterion;
import com.bstek.dorado.data.provider.filter.SingleValueFilterCriterion;
import com.quotamanagesys.interceptor.CalculateCore;
import com.quotamanagesys.interceptor.ResultTableCreator;
import com.quotamanagesys.model.LightItem;
import com.quotamanagesys.model.QuotaFormulaResult;
import com.quotamanagesys.model.QuotaFormulaResultValue;
import com.quotamanagesys.model.QuotaItem;
import com.quotamanagesys.model.QuotaLevel;
import com.quotamanagesys.model.QuotaTargetValue;
import com.quotamanagesys.tools.CriteriaConvertCore;

@Component
public class QuotaItemDao extends HibernateDao {

	@Resource
	QuotaFormulaResultValueDao quotaFormulaResultValueDao;
	@Resource
	QuotaTargetValueDao quotaTargetValueDao;
	@Resource
	QuotaLevelDao quotaLevelDao;
	@Resource
	CalculateCore calculateCore;
	@Resource
	ResultTableCreator resultTableCreator;
	@Resource
	LightItemDao lightItemDao;
	@Resource
	CriteriaConvertCore criteriaConvertCore;

	@DataProvider
	public Collection<QuotaItem> getAll() {
		String hqlString = "from " + QuotaItem.class.getName()
				+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
				+"quotaItemCreator.quotaCover.sort asc";
		Collection<QuotaItem> quotaItems = this.query(hqlString);
		return quotaItems;
	}
	
	@DataProvider
	public QuotaItem getQuotaItem(String id) {
		String hqlString = "from " + QuotaItem.class.getName() + " where id='"+ id + "'";
		List<QuotaItem> quotaItems = this.query(hqlString);
		if (quotaItems.size() > 0) {
			return quotaItems.get(0);
		} else {
			return null;
		}
	}
	
	@DataProvider
	public Collection<QuotaItem> getQuotaItemsByManageDept(String manageDeptId) {
		String hqlString = "from " + QuotaItem.class.getName()
				+ " where quotaItemCreator.quotaType.manageDept.id='"+ manageDeptId 
				+ "' order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
				+"quotaItemCreator.quotaCover.sort asc";
		Collection<QuotaItem> quotaItems = this.query(hqlString);
		return quotaItems;
	}
	
	//��ҳ��ʽ��ѯ����ָ��
	@DataProvider
	public void getQuotaItemsOfLastMonthByManageDeptWithPage(Page<QuotaItem> page,Criteria criteria,String manageDeptId) throws Exception {
		if (manageDeptId!=null) {
			String filterString=criteriaConvertCore.convertToSQLString(criteria);
			if (!filterString.equals("")) {
				filterString=" and ("+filterString+")";
			}
			
			Calendar calendar=Calendar.getInstance();	
			//��ȡִ��ʱ������
			int year=calendar.get(Calendar.YEAR);
			int month=calendar.get(Calendar.MONTH);//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
			
			//ȥ��12�����
			if (month==0) {
				year=year-1;
				month=12;
			}
			
			String hqlString = "from " + QuotaItem.class.getName()
					+ " where quotaItemCreator.quotaType.manageDept.id='"
					+ manageDeptId + "' and year="+year+" and month="+month+filterString
					+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
					+"quotaItemCreator.quotaCover.sort asc";
			this.pagingQuery(page, hqlString, "select count(*)" + hqlString);
		} else {
			System.out.print("����Ϊ��");
		}
	}
	
	@DataProvider
	public Collection<QuotaItem> getQuotaItemsByManageDept_Month(String manageDeptId) {
		String hqlString = "from " + QuotaItem.class.getName()
				+ " where quotaItemCreator.quotaType.manageDept.id='"
				+ manageDeptId 
				+ "' and quotaItemCreator.quotaType.rate='��'"
				+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
				+"quotaItemCreator.quotaCover.sort asc";
		Collection<QuotaItem> quotaItems = this.query(hqlString);
		return quotaItems;
	}
	
	//��ҳ��ʽ��ѯ
	@DataProvider
	public void getQuotaItemsByManageDept_MonthWithPage(Page<QuotaItem> page,Criteria criteria,String manageDeptId) throws Exception{
		if (manageDeptId!=null) {
			String filterString=criteriaConvertCore.convertToSQLString(criteria);
			if (!filterString.equals("")) {
				filterString=" and ("+filterString+")";
			}
			
			String hqlString = "from " + QuotaItem.class.getName()
					+ " where quotaItemCreator.quotaType.manageDept.id='"
					+ manageDeptId 
					+ "' and quotaItemCreator.quotaType.rate='��'"+filterString
					+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
					+"quotaItemCreator.quotaCover.sort asc";
			this.pagingQuery(page, hqlString, "select count(*)" + hqlString);
		} else {
			System.out.print("����Ϊ��");
		}
	}
	
	//��ҳ��ѯ
	@DataProvider
	public void getQuotaItemsNotAllowSubmitByManageDeptWithPage(Page<QuotaItem> page,Criteria criteria,String manageDeptId,String rate) throws Exception{
		if (manageDeptId!=null) {
			String filterString=criteriaConvertCore.convertToSQLString(criteria);
			if (!filterString.equals("")) {
				filterString=" and ("+filterString+")";
			}
			
			Calendar calendar=Calendar.getInstance();	
			//��ȡִ��ʱ������
			int year=calendar.get(Calendar.YEAR);
			int month=calendar.get(Calendar.MONTH);//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
			
			//ȥ��12�����
			if (month==0) {
				year=year-1;
			}
			
			String hqlString;
			
			if (rate.equals("��")) {
				//�ڶ���һ�¿ɼ��������ָ��
				if (month==0) {
					hqlString = "from " + QuotaItem.class.getName()
							+ " where allowSubmit=false and quotaItemCreator.quotaType.manageDept.id='"
							+ manageDeptId + "' and year="+year+" and month=0"+filterString+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
							+"quotaItemCreator.quotaCover.sort asc";
					this.pagingQuery(page, hqlString, "select count(*)" + hqlString);
				}else {
					System.out.print("��ʱ��β��ɲ鿴�������ָ��");
				}
			} else {
				//ȥ��12�����
				if (month==0) {
					month=12;
				}
				hqlString = "from " + QuotaItem.class.getName()
						+ " where allowSubmit=false and quotaItemCreator.quotaType.manageDept.id='"
						+ manageDeptId + "' and year="+year+" and month="+month+filterString
						+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
						+"quotaItemCreator.quotaCover.sort asc";
				this.pagingQuery(page, hqlString, "select count(*)" + hqlString);
			}
			
		} else {
			System.out.print("����Ϊ��");
		}
	}
	
	@DataProvider
	public void getQuotaItemsOfLastYearByManageDeptWithPage(Page<QuotaItem> page,Criteria criteria,String manageDeptId) throws Exception {
		if (manageDeptId!=null) {
			String filterString=criteriaConvertCore.convertToSQLString(criteria);
			if (!filterString.equals("")) {
				filterString=" and ("+filterString+")";
			}
			
			Calendar calendar=Calendar.getInstance();	
			//��ȡִ��ʱ������
			int year=calendar.get(Calendar.YEAR);
			int month=calendar.get(Calendar.MONTH);//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
			
			//�ڶ���һ�¿ɼ��������ָ��
			if (month==0) {
				year=year-1;
				String hqlString = "from " + QuotaItem.class.getName()
						+ " where quotaItemCreator.quotaType.manageDept.id='"
						+ manageDeptId 
						+ "' and year="+year+" and quotaItemCreator.quotaType.rate='��'"+filterString
						+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
						+"quotaItemCreator.quotaCover.sort asc";
				this.pagingQuery(page, hqlString, "select count(*)" + hqlString);
			}else {
				System.out.print("��ʱ��β��ɲ鿴�������ָ��");
			}
		} else {
			System.out.print("����Ϊ��");
		}
		
	}
	
	@DataProvider
	public Collection<QuotaItem> getQuotaItemsByManageDept_Year(String manageDeptId) {
		Calendar calendar=Calendar.getInstance();	
		//��ȡִ��ʱ������
		int year=calendar.get(Calendar.YEAR);
		int month=calendar.get(Calendar.MONTH);//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
		
		//�ڶ���һ�¿ɼ��������ָ��
		if (month==0) {
			year=year-1;
			String hqlString = "from " + QuotaItem.class.getName()
					+ " where quotaItemCreator.quotaType.manageDept.id='"
					+ manageDeptId 
					+ "' and year="+year+" and quotaItemCreator.quotaType.rate='��'"
					+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
					+"quotaItemCreator.quotaCover.sort asc";
			Collection<QuotaItem> quotaItems = this.query(hqlString);
			return quotaItems;
		}else {
			return null;
		}
	}

	@DataProvider
	public Collection<QuotaItem> getQuotaItemsByManageDeptAndTopLevel(String manageDeptId) {
		Collection<QuotaLevel> quotaLevels=quotaLevelDao.getAll();
		int highestLevel=-1;
		for (QuotaLevel quotaLevel : quotaLevels) {
			if (highestLevel==-1) {
				highestLevel=quotaLevel.getLevel();
			}else{
				if (highestLevel>quotaLevel.getLevel()) {
					highestLevel=quotaLevel.getLevel();
				}
			}
		}
		
		String hqlString = "from " + QuotaItem.class.getName()
				+ " where quotaItemCreator.quotaType.manageDept.id='"+ manageDeptId 
				+ "' and quotaItemCreator.quotaType.quotaLevel.level='"+highestLevel+"'"
				+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
				+"quotaItemCreator.quotaCover.sort asc";
		Collection<QuotaItem> quotaItems = this.query(hqlString);
		return quotaItems;
	}
	
	@DataProvider
	public Collection<QuotaItem> getQuotaItemsByYear(int year) {
		String hqlString = "from " + QuotaItem.class.getName() + " where year="
				+ year
				+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
				+"quotaItemCreator.quotaCover.sort asc";
		Collection<QuotaItem> quotaItems = this.query(hqlString);
		return quotaItems;
	}

	@DataProvider
	public Collection<QuotaItem> getQuotaItemsByQuotaType(String quotaTypeId) {
		String hqlString = "from " + QuotaItem.class.getName()
				+ " where quotaItemCreator.quotaType.id='" + quotaTypeId + "'"
				+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
				+"quotaItemCreator.quotaCover.sort asc";
		Collection<QuotaItem> quotaItems = this.query(hqlString);
		return quotaItems;
	}

	@DataProvider
	public Collection<QuotaItem> getQuotaItemsByQuotaCover(String quotaCoverId) {
		String hqlString = "from " + QuotaItem.class.getName()
				+ " where quotaItemCreator.quotaCover.id='" + quotaCoverId
				+ "'"
				+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
				+"quotaItemCreator.quotaCover.sort asc";
		Collection<QuotaItem> quotaItems = this.query(hqlString);
		return quotaItems;
	}

	@DataProvider
	public Collection<QuotaItem> getQuotaItemsByRate(String rate) {
		String hqlString = "from " + QuotaItem.class.getName()
				+ " where quotaItemCreator.quotaType.rate='" + rate + "'"
				+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
				+"quotaItemCreator.quotaCover.sort asc";
		Collection<QuotaItem> quotaItems = this.query(hqlString);
		return quotaItems;
	}

	@DataProvider
	public Collection<QuotaItem> getQuotaItemsByQuotaItemCreator(
			String quotaItemCreatorId) {
		String hqlString = "from " + QuotaItem.class.getName()
				+ " where quotaItemCreator.id='" + quotaItemCreatorId + "'"
				+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
				+"quotaItemCreator.quotaCover.sort asc";
		Collection<QuotaItem> quotaItems = this.query(hqlString);
		return quotaItems;
	}

	@DataProvider
	public Collection<QuotaItem> getQuotaItemsByDutyDept(String dutyDeptId) {
		String hqlString = "from " + QuotaItem.class.getName()
				+ " where quotaItemCreator.quotaDutyDept.id='" + dutyDeptId
				+ "'"
				+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
				+"quotaItemCreator.quotaCover.sort asc";
		Collection<QuotaItem> quotaItems = this.query(hqlString);
		return quotaItems;
	}
	
	@DataProvider
	public Collection<QuotaItem> getQuotaItemsWithRedLight(Collection<QuotaItem> quotaItems){
		Collection<LightItem> lightItems=lightItemDao.getAll();
		Collection<QuotaItem> quotaItemsWithRedLight=new ArrayList<QuotaItem>();
		
		for (QuotaItem quotaItem : quotaItems) {
			Collection<QuotaFormulaResultValue> quotaFormulaResultValues=quotaFormulaResultValueDao.getQuotaFormulaResultValuesByQuotaItem(quotaItem.getId());
			if (quotaFormulaResultValues.size()>0) {
				for (QuotaFormulaResultValue quotaFormulaResultValue : quotaFormulaResultValues) {
					boolean isAdded=false;
					QuotaFormulaResult quotaFormulaResult=quotaFormulaResultValue.getQuotaFormulaResult();
					for (LightItem lightItem : lightItems) {
						if ((quotaFormulaResult.getId()).equals(lightItem.getQuotaFormulaResult().getId())) {
							if ((quotaFormulaResultValue.getValue()).equals("0.0")) {
								quotaItemsWithRedLight.add(quotaItem);
								isAdded=true;
								break;
							}else {
								break;
							}
						} else {
							continue;
						}
					}
					if (isAdded==true) {
						break;
					} else {
						continue;
					}
				}
			}
		}
		
		return quotaItemsWithRedLight;
	}
	
	@DataProvider
	public Collection<QuotaItem> getQuotaItemsWithRedLightByManageDept(String manageDeptId,String rate){
		Calendar calendar=Calendar.getInstance();	
		//��ȡִ��ʱ������
		int year=calendar.get(Calendar.YEAR);
		int month=calendar.get(Calendar.MONTH);//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
		
		//ȥ��12�����
		if (month==0) {
			year=year-1;
			month=12;
		}
		
		String hqlString;
		
		if (rate.equals("��")) {
			hqlString = "from " + QuotaItem.class.getName()
					+ " where quotaItemCreator.quotaType.manageDept.id='"
					+ manageDeptId + "' and year="+year+" and month=0 order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
					+"quotaItemCreator.quotaCover.sort asc";
		} else {
			hqlString = "from " + QuotaItem.class.getName()
					+ " where quotaItemCreator.quotaType.manageDept.id='"
					+ manageDeptId + "' and year="+year+" and month="+month
					+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
					+"quotaItemCreator.quotaCover.sort asc";
		}
		
		
		Collection<QuotaItem> quotaItems = this.query(hqlString);
		Collection<QuotaItem> quotaItemsWithRedLight=getQuotaItemsWithRedLight(quotaItems);
		
		return quotaItemsWithRedLight;
	}
	
	//�����Ż�ȡδ�����ύ�ľ���ָ��
	@DataProvider
	public Collection<QuotaItem> getQuotaItemsNotAllowSubmitByManageDept(String manageDeptId,String rate){
		Calendar calendar=Calendar.getInstance();	
		//��ȡִ��ʱ������
		int year=calendar.get(Calendar.YEAR);
		int month=calendar.get(Calendar.MONTH);//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
		
		//ȥ��12�����
		if (month==0) {
			year=year-1;
		}
		
		String hqlString;
		
		if (rate.equals("��")) {
			//�ڶ���һ�¿ɼ��������ָ��
			if (month==0) {
				hqlString = "from " + QuotaItem.class.getName()
						+ " where allowSubmit=false and quotaItemCreator.quotaType.manageDept.id='"
						+ manageDeptId + "' and year="+year+" and month=0 order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
						+"quotaItemCreator.quotaCover.sort asc";
				Collection<QuotaItem> quotaItems = this.query(hqlString);
				return quotaItems;
			}else {
				return null;
			}
			
		} else {
			//ȥ��12�����
			if (month==0) {
				month=12;
			}
			hqlString = "from " + QuotaItem.class.getName()
					+ " where allowSubmit=false and quotaItemCreator.quotaType.manageDept.id='"
					+ manageDeptId + "' and year="+year+" and month="+month
					+" order by quotaItemCreator.quotaType.quotaLevel.level asc,quotaItemCreator.name,year asc,month asc,"
					+"quotaItemCreator.quotaCover.sort asc";
			Collection<QuotaItem> quotaItems = this.query(hqlString);
			return quotaItems;
		}
	}

	@DataResolver
	public void saveQuotaItems(Collection<QuotaItem> quotaItems) {
		Session session = this.getSessionFactory().openSession();
		Collection<QuotaItem> updateQuotaItems=new ArrayList<QuotaItem>();
		Collection<QuotaItem> calculateQuotaItems=new ArrayList<QuotaItem>();
		
		Calendar calendar=Calendar.getInstance();
		int currentYear=calendar.get(Calendar.YEAR);//
		int currentMonth=calendar.get(Calendar.MONTH)+1;//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
		int currentDate=calendar.get(Calendar.DATE);//
		Date thisDate=calendar.getTime();
		IUser loginuser = ContextHolder.getLoginUser();
		
		try {
			for (QuotaItem quotaItem : quotaItems) {
				EntityState state = EntityUtils.getState(quotaItem);
				if (state.equals(EntityState.NEW)) {
					session.save(quotaItem);
				} else if (state.equals(EntityState.MODIFIED)) {
					boolean isWantToBeCalculated=false;
					QuotaItem thisQuotaItem = getQuotaItem(quotaItem.getId());
					if (quotaItem.getFinishValue() != null) {
						if (thisQuotaItem.getFinishValue()==null) {
							thisQuotaItem.setFinishValue(quotaItem.getFinishValue());
							isWantToBeCalculated=true;
						}else {
							if (!thisQuotaItem.getFinishValue().equals(quotaItem.getFinishValue())) {
								thisQuotaItem.setFinishValue(quotaItem.getFinishValue());
								isWantToBeCalculated=true;
							}
						}
						
					}
					if (quotaItem.getAccumulateValue()!=null) {
						if (thisQuotaItem.getAccumulateValue()==null) {
							thisQuotaItem.setAccumulateValue(quotaItem.getAccumulateValue());
							isWantToBeCalculated=true;
						} else {
							if (!thisQuotaItem.getAccumulateValue().equals(quotaItem.getAccumulateValue())) {
								thisQuotaItem.setAccumulateValue(quotaItem.getAccumulateValue());
								isWantToBeCalculated=true;
							}
						}
						
					}
					if (quotaItem.getSameTermValue()!=null) {
						if (thisQuotaItem.getSameTermValue()==null) {
							thisQuotaItem.setSameTermValue(quotaItem.getSameTermValue());
							isWantToBeCalculated=true;
						} else {
							if (!thisQuotaItem.getSameTermValue().equals(quotaItem.getSameTermValue())) {
								thisQuotaItem.setSameTermValue(quotaItem.getSameTermValue());
								isWantToBeCalculated=true;
							}
						}
						
					}
					if (quotaItem.getSameTermAccumulateValue()!=null) {
						if (thisQuotaItem.getSameTermAccumulateValue()==null) {
							thisQuotaItem.setSameTermAccumulateValue(quotaItem.getSameTermAccumulateValue());
							isWantToBeCalculated=true;
						} else {
							if (!thisQuotaItem.getSameTermAccumulateValue().equals(quotaItem.getSameTermAccumulateValue())) {
								thisQuotaItem.setSameTermAccumulateValue(quotaItem.getSameTermAccumulateValue());
								isWantToBeCalculated=true;
							}
						}
						
					}
					
					//��д��ʱ״̬���á���¼��һ����дʱ�䡢���һ����дʱ�䡢�����д��Ա����
					if (thisQuotaItem.getFirstSubmitTime()==null) {
						thisQuotaItem.setFirstSubmitTime(thisDate);
						
						if (thisQuotaItem.getQuotaItemCreator().getQuotaType().getRate().equals("��")) {
							if ((currentMonth<=1)&&(thisQuotaItem.getYear()==(currentYear-1))) {
								if (currentDate>5) {
									thisQuotaItem.setOverTime(true);
								}
							}
						}else if (thisQuotaItem.getQuotaItemCreator().getQuotaType().getRate().equals("��")) {
							if ((thisQuotaItem.getMonth()==(currentMonth-1))&&(thisQuotaItem.getYear()==currentYear)) {
								if (currentDate>5) {
									thisQuotaItem.setOverTime(true);
								}
							}else if ((thisQuotaItem.getMonth()<(currentMonth-1))&&(thisQuotaItem.getYear()==currentYear)) {
								thisQuotaItem.setOverTime(true);
							}
						}
					}
					
					thisQuotaItem.setLastSubmitTime(thisDate);
					if (loginuser == null) {
						throw new NoneLoginException("Please login first!");
					}else {
						thisQuotaItem.setUsernameOfLastSubmit(loginuser.getCname());
					}
					
					if (quotaItem.getRedLightReason() != null) {
						thisQuotaItem.setRedLightReason(quotaItem.getRedLightReason());
					}

					session.merge(thisQuotaItem);
					session.flush();
					session.clear();
					if (isWantToBeCalculated) {
						calculateQuotaItems.add(thisQuotaItem);
					}
					updateQuotaItems.add(thisQuotaItem);
				} else if (state.equals(EntityState.DELETED)) {					
					//����ɾ��QuotaFormulaResultValue
					Collection<QuotaFormulaResultValue> quotaFormulaResultValues = quotaFormulaResultValueDao
							.getQuotaFormulaResultValuesByQuotaItem(quotaItem.getId());
					quotaFormulaResultValueDao.deleteQuotaFormulaResultValues(quotaFormulaResultValues);
					
					//����ɾ��QuotaTargetValue
					Collection<QuotaTargetValue> quotaTargetValues=quotaTargetValueDao
							.getQuotaTargetValuesByQuotaItem(quotaItem.getId());
					quotaTargetValueDao.deleteQuotaTargetValues(quotaTargetValues);

					quotaItem.setQuotaItemCreator(null);
					session.delete(quotaItem);
					session.flush();
					session.clear();
				}
			}
			
			calculateCore.calculate(calculateQuotaItems);
			setAllowSubmitStatus(updateQuotaItems);
			resultTableCreator.createOrUpdateResultTable(updateQuotaItems);
		} catch (Exception e) {
			System.out.print(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}
	
	//����дֵ�и��µľ���ָ�����allowSubmit������
	@Expose
	public void setAllowSubmitStatus(Collection<QuotaItem> quotaItems){
		Session session = this.getSessionFactory().openSession();
		Collection<LightItem> lightItems=lightItemDao.getAll();
		try {
			for (QuotaItem quotaItem : quotaItems) {
				if (((!quotaItem.getFinishValue().equals(""))&&(quotaItem.getFinishValue()!=null))
				&&((!quotaItem.getSameTermValue().equals(""))&&(quotaItem.getSameTermValue()!=null))
				&&((!quotaItem.getAccumulateValue().equals(""))&&(quotaItem.getAccumulateValue()!=null))
				&&((!quotaItem.getSameTermAccumulateValue().equals(""))&&(quotaItem.getSameTermAccumulateValue()!=null))) {
					quotaItem.setAllowSubmit(true);
					Collection<QuotaFormulaResultValue> quotaFormulaResultValues=quotaFormulaResultValueDao.getQuotaFormulaResultValuesByQuotaItem(quotaItem.getId());
					if (quotaFormulaResultValues.size()>0) {
						boolean isAllGreenLight=true;
						for (QuotaFormulaResultValue quotaFormulaResultValue : quotaFormulaResultValues) {
							QuotaFormulaResult quotaFormulaResult=quotaFormulaResultValue.getQuotaFormulaResult();
							for (LightItem lightItem : lightItems) {
								if ((quotaFormulaResult.getId()).equals(lightItem.getQuotaFormulaResult().getId())) {
									if ((quotaFormulaResultValue.getValue()).equals("0.0")) {
										isAllGreenLight=false;
										if (quotaItem.getRedLightReason()!=null) {
											if (quotaItem.getRedLightReason().equals("")) {
												quotaItem.setAllowSubmit(false);
											}
										}else {
											quotaItem.setAllowSubmit(false);
										}
										break;
									}else {
										break;
									}
								} else {
									continue;
								}
							}
						}
						if (isAllGreenLight==true) {
							quotaItem.setRedLightReason(null);
						}
					}
				}else {
					Collection<QuotaFormulaResultValue> quotaFormulaResultValues=quotaFormulaResultValueDao.getQuotaFormulaResultValuesByQuotaItem(quotaItem.getId());
					if (quotaFormulaResultValues.size()>0) {
						boolean isAllGreenLight=true;
						for (QuotaFormulaResultValue quotaFormulaResultValue : quotaFormulaResultValues) {
							QuotaFormulaResult quotaFormulaResult=quotaFormulaResultValue.getQuotaFormulaResult();
							for (LightItem lightItem : lightItems) {
								if ((quotaFormulaResult.getId()).equals(lightItem.getQuotaFormulaResult().getId())) {
									if ((quotaFormulaResultValue.getValue()).equals("0.0")) {
										isAllGreenLight=false;
										break;
									}else {
										break;
									}
								} else {
									continue;
								}
							}
						}
						if (isAllGreenLight==true) {
							quotaItem.setRedLightReason(null);
						}
					}
					quotaItem.setAllowSubmit(false);
				}
				
				session.merge(quotaItem);
				session.flush();
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
	
	@DataResolver
	public void deleteQuotaItems(Collection<QuotaItem> quotaItems) {
		Session session = this.getSessionFactory().openSession();
		try {
			//ɾ��quotaItemʱ�����Ȼ���quota_item_view_xxxx��ɾ����Ӧ�ļ�¼
			resultTableCreator.deleteItemsFromResultTable(quotaItems);
			for (QuotaItem quotaItem : quotaItems) {
				//����ɾ��QuotaFormulaResultValue
				Collection<QuotaFormulaResultValue> quotaFormulaResultValues = quotaFormulaResultValueDao
						.getQuotaFormulaResultValuesByQuotaItem(quotaItem.getId());
				quotaFormulaResultValueDao.deleteQuotaFormulaResultValues(quotaFormulaResultValues);
				
				//����ɾ��QuotaTargetValue
				Collection<QuotaTargetValue> quotaTargetValues=quotaTargetValueDao
						.getQuotaTargetValuesByQuotaItem(quotaItem.getId());
				quotaTargetValueDao.deleteQuotaTargetValues(quotaTargetValues);

				quotaItem.setQuotaItemCreator(null);
				session.delete(quotaItem);
				session.flush();
				session.clear();
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}
	
	//�������Ƶ�ʲ���ȷ�ľ���ָ�꣨����ڹ����ţ�
	@Expose
	public void deleteQuotaItemsWithWrongRateByManageDept(String manageDeptId){
		Session session = this.getSessionFactory().openSession();
		try {
			String hqlString="from "+QuotaItem.class.getName()+" where (quotaItemCreator.quotaType.rate='��'"
					+" and quotaItemCreator.quotaType.manageDept.id='"+manageDeptId+"'"
					+" and month<>0)"
					+" or (quotaItemCreator.quotaType.rate='��'"
					+" and quotaItemCreator.quotaType.manageDept.id='"+manageDeptId+"'"
					+" and month=0)";
			Collection<QuotaItem> wrongRateItems=this.query(hqlString);
			deleteQuotaItems(wrongRateItems);
		} catch (Exception e) {
			System.out.print(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}
	
	//���δ����Ŀ��ֵ�ľ���ָ�꣨ȫ����
	@Expose
	public void deleteAllQuotaItemsWithoutTargetValues(){
		Collection<QuotaItem> quotaItems=getAll();
		Collection<QuotaItem> quotaItemsWithoutTargetValues=new ArrayList<QuotaItem>();
		for (QuotaItem quotaItem : quotaItems) {
			if ((quotaItem.getQuotaItemCreator().getQuotaType().getRate()).equals("��")) {
				if ((quotaTargetValueDao.getQuotaTargetValuesByQuotaItem(quotaItem.getId())).size()==0) {
					quotaItemsWithoutTargetValues.add(quotaItem);
				}
			}
		}
		deleteQuotaItems(quotaItemsWithoutTargetValues);
	}

	@DataResolver
	public void excuteHQL(String HQL) {
		Session session = this.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();
		try {
			tx.begin();
			Query query = session.createQuery(HQL);
			query.executeUpdate();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			System.out.println(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}

}
