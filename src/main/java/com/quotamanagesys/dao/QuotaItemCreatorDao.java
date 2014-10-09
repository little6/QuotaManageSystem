package com.quotamanagesys.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.business.IDept;
import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.core.exception.NoneLoginException;
import com.bstek.bdf2.core.model.DefaultDept;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.quotamanagesys.model.FormulaParameter;
import com.quotamanagesys.model.QuotaCover;
import com.quotamanagesys.model.QuotaFormula;
import com.quotamanagesys.model.QuotaItem;
import com.quotamanagesys.model.QuotaItemCreator;
import com.quotamanagesys.model.QuotaProperty;
import com.quotamanagesys.model.QuotaPropertyValue;
import com.quotamanagesys.model.QuotaTargetValue;
import com.quotamanagesys.model.QuotaType;
import com.quotamanagesys.model.QuotaTypeFormulaLink;

@Component
public class QuotaItemCreatorDao extends HibernateDao {

	@Resource
	QuotaPropertyValueDao quotaPropertyValueDao;
	@Resource
	QuotaFormulaDao quotaFormulaDao;
	@Resource
	QuotaPropertyDao quotaPropertyDao;
	@Resource
	QuotaItemDao quotaItemDao;
	@Resource
	QuotaTypeDao quotaTypeDao;
	@Resource
	QuotaCoverDao quotaCoverDao;
	@Resource
	QuotaTypeFormulaLinkDao quotaTypeFormulaLinkDao;
	@Resource
	DepartmentDao departmentDao;

	@DataProvider
	public Collection<QuotaItemCreator> getAll() {
		String hqlString = "from " + QuotaItemCreator.class.getName()+" order by quotaType.quotaLevel.level asc,quotaType.name asc,quotaCover.sort asc";
		Collection<QuotaItemCreator> quotaItemCreators = this.query(hqlString);
		return quotaItemCreators;
	}

	@DataProvider
	public QuotaItemCreator getQuotaItemCreator(String id) {
		String hqlString = "from " + QuotaItemCreator.class.getName()
				+ " where id='" + id + "'";
		List<QuotaItemCreator> quotaItemCreators = this.query(hqlString);
		if (quotaItemCreators.size() > 0) {
			return quotaItemCreators.get(0);
		} else {
			return null;
		}
	}

	@DataProvider
	public Collection<QuotaItemCreator> getQuotaItemCreatorsByQuotaType(
			String quotaTypeId) {
		String hqlString = "from " + QuotaItemCreator.class.getName()
				+ " where quotaType.id='" + quotaTypeId + "'"
				+" order by quotaType.quotaLevel.level asc,quotaType.name asc,quotaCover.sort asc";
		Collection<QuotaItemCreator> quotaItemCreators = this.query(hqlString);
		return quotaItemCreators;
	}

	@DataProvider
	public Collection<QuotaItemCreator> getQuotaItemCreatorsByYear(int year) {
		String hqlString = "from " + QuotaItemCreator.class.getName()
				+ " where year=" + year
				+" order by quotaType.quotaLevel.level asc,quotaType.name asc,quotaCover.sort asc";
		Collection<QuotaItemCreator> quotaItemCreators = this.query(hqlString);
		return quotaItemCreators;
	}

	@DataProvider
	public Collection<QuotaItemCreator> getQuotaItemCreatorsByManageDept(
			String manageDeptId) throws Exception {
		String hqlString = "from " + QuotaItemCreator.class.getName()
				+ " where quotaType.manageDept.id='" + manageDeptId + "'"
				+" order by quotaType.quotaLevel.level asc,quotaType.name asc,quotaCover.sort asc";
		Collection<QuotaItemCreator> quotaItemCreators = this.query(hqlString);
		List<QuotaItemCreator> results = new ArrayList<QuotaItemCreator>();
		for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
			Collection<QuotaPropertyValue> quotaPropertyValues = quotaPropertyValueDao
					.getQuotaPropertyValuesByQuotaItemCreator(quotaItemCreator.getId());
			String quotaPropertiesNames = "";
			for (QuotaPropertyValue quotaPropertyValue : quotaPropertyValues) {
				if (quotaPropertiesNames == "") {
					quotaPropertiesNames = quotaPropertyValue.getQuotaProperty().getName();
				} else {
					quotaPropertiesNames = quotaPropertiesNames + ","+ quotaPropertyValue.getQuotaProperty().getName();
				}
			}
			results.add(quotaItemCreator);
			/*
			QuotaItemCreator targetQuotaItemCreator = EntityUtils
					.toEntity(quotaItemCreator);
			EntityUtils.setValue(targetQuotaItemCreator,
					"quotaPropertiesNames", quotaPropertiesNames);
			results.add(targetQuotaItemCreator);
			*/
		}
		return results;
	}

	@DataProvider
	public Collection<QuotaItemCreator> getQuotaItemCreatorsByQuotaCover(
			String quotaCoverId) {
		IUser loginUser = ContextHolder.getLoginUser();
		if (loginUser.isAdministrator()) {
			String hqlString = "from " + QuotaItemCreator.class.getName()
					+ " where quotaCover.id='" + quotaCoverId + "'"
					+" order by quotaType.quotaLevel.level asc,quotaType.name asc,quotaCover.sort asc";
			Collection<QuotaItemCreator> quotaItemCreators = this.query(hqlString);
			return quotaItemCreators;
		}else {
			List<IDept> iDepts=loginUser.getDepts();
			
			String hqlString = "from " + QuotaItemCreator.class.getName()
					+ " where quotaCover.id='" + quotaCoverId + "' and quotaType.manageDept.id='"+iDepts.get(0).getId()
					+"' order by quotaType.quotaLevel.level asc,quotaType.name asc,quotaCover.sort asc";
			Collection<QuotaItemCreator> quotaItemCreators = this.query(hqlString);
			return quotaItemCreators;
		}
	}
	
	//��ȡ�ھ�Ϊ��һ���ھ��ķֽ�ָ��
	@DataProvider
	public Collection<QuotaItemCreator> getSplitQuotaItemCreators(String id){
		QuotaItemCreator thisQuotaItemCreator=getQuotaItemCreator(id);
		QuotaCover thisQuotaCover=thisQuotaItemCreator.getQuotaCover();
		QuotaType thisQuotaType=thisQuotaItemCreator.getQuotaType();
		int year=thisQuotaItemCreator.getYear();
		Collection<QuotaCover> childrenQuotaCovers=quotaCoverDao.getQuotaCoversByFatherCover(thisQuotaCover.getId());
		
		Collection<QuotaItemCreator> splitQuotaItemCreators=new ArrayList<QuotaItemCreator>();
		
		for (QuotaCover childQuotaCover : childrenQuotaCovers) {
			String hqlString="from "+QuotaItemCreator.class.getName()+" where quotaCover.id='"+childQuotaCover.getId()+"'"
					+" and year="+year+" and quotaType.id='"+thisQuotaType.getId()+"'"
					+" order by quotaType.quotaLevel.level asc,quotaType.name asc,quotaCover.sort asc";
			Collection<QuotaItemCreator> quotaItemCreators=this.query(hqlString);
			if (quotaItemCreators.size()>0) {
				splitQuotaItemCreators.addAll(quotaItemCreators);
			}
		}
		return splitQuotaItemCreators;
	}

	@DataProvider
	public Collection<QuotaItemCreator> getQuotaItemCreatorsByFormula(
			String quotaFormulaId) throws Exception {
		Session session = this.getSessionFactory().openSession();
		List<QuotaItemCreator> quotaItemCreators = session
				.createCriteria(QuotaItemCreator.class)
				.createAlias("quotaFormulas", "q")
				.add(Restrictions.eq("q.id", quotaFormulaId)).list();
		session.flush();
		session.close();
		List<QuotaItemCreator> results = new ArrayList<QuotaItemCreator>();
		for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
			Collection<QuotaPropertyValue> quotaPropertyValues = quotaPropertyValueDao
					.getQuotaPropertyValuesByQuotaItemCreator(quotaItemCreator
							.getId());
			String quotaPropertiesNames = "";
			for (QuotaPropertyValue quotaPropertyValue : quotaPropertyValues) {
				if (quotaPropertiesNames == "") {
					quotaPropertiesNames = quotaPropertyValue
							.getQuotaProperty().getName();
				} else {
					quotaPropertiesNames = quotaPropertiesNames + ","
							+ quotaPropertyValue.getQuotaProperty().getName();
				}
			}
			QuotaItemCreator targetQuotaItemCreator = EntityUtils
					.toEntity(quotaItemCreator);
			EntityUtils.setValue(targetQuotaItemCreator,
					"quotaPropertiesNames", quotaPropertiesNames);
			results.add(targetQuotaItemCreator);
		}
		return results;
	}
	
	//��ȡ��δ�������Ŀ��ֵ��ָ��������
	@DataProvider
	public Collection<QuotaItemCreator> getQuotaItemCreatorsWithoutQuotaPropertyValues() throws Exception{
		Collection<QuotaItemCreator> quotaItemCreators;
		IUser loginUser = ContextHolder.getLoginUser();
		if (loginUser.isAdministrator()) {
			quotaItemCreators=getAll();
		}else {
			List<IDept> iDepts=loginUser.getDepts();
			quotaItemCreators=getQuotaItemCreatorsByManageDept(iDepts.get(0).getId());
		}
		
		Collection<QuotaItemCreator> quotaItemCreatorsWithoutPropertyValues=new ArrayList<QuotaItemCreator>();
		
		for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
			Collection<QuotaPropertyValue> quotaPropertyValues=quotaPropertyValueDao.getQuotaPropertyValuesByQuotaItemCreator(quotaItemCreator.getId());
			if (quotaPropertyValues.size()>0) {
				continue;
			}else {
				quotaItemCreatorsWithoutPropertyValues.add(quotaItemCreator);
			}
		}
		return quotaItemCreatorsWithoutPropertyValues;
	}

	//����ָ��������еĵ�ǰ����ָ���ʼ������ھ�Ϊ�����ھ���ָ��������
	@Expose
	public void createQuotaItemCreatorsByTopCover(){
		QuotaCover topQuotaCover=quotaCoverDao.getTopQuotaCovers().get(0);
		Collection<QuotaType> quotaTypesInUsed=quotaTypeDao.getQuotaTypesInUsed();
		
		Calendar calendar=Calendar.getInstance();	
		//��ȡִ��ʱ������
		int year=calendar.get(Calendar.YEAR);
		int month=calendar.get(Calendar.MONTH)+1;//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
		
		Session session=this.getSessionFactory().openSession();
		try {
			for (QuotaType quotaTypeInUsed : quotaTypesInUsed) {
				createQuotaItemCreator(quotaTypeInUsed,topQuotaCover,quotaTypeInUsed.getManageDept(),year,session);
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
	
	//�������ж����ھ���ָ��������������һ���ھ�ָ��������
	@Expose
	public void createAllFirstChildLevelQuotaItemCreators(){
		QuotaCover topQuotaCover=quotaCoverDao.getTopQuotaCovers().get(0);
		Collection<QuotaItemCreator> quotaItemCreators=getQuotaItemCreatorsByQuotaCover(topQuotaCover.getId());
		createFirstChildLevelQuotaItemCreators(quotaItemCreators);
	}
	
	//������ѡָ��������������һ���ھ�ָ��������
	@Expose
	public void createFirstChildLevelQuotaItemCreators(Collection<QuotaItemCreator> quotaItemCreators){
		Session session=this.getSessionFactory().openSession();
		try {
			for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
				QuotaType quotaType=quotaItemCreator.getQuotaType();
				QuotaCover quotaCover=quotaItemCreator.getQuotaCover();
				Collection<QuotaCover> firstLevelChildrenQuotaCovers=quotaCoverDao.getQuotaCoversByFatherCover(quotaCover.getId());
				for (QuotaCover firstLevelChildQuotaCover : firstLevelChildrenQuotaCovers) {
					Collection<DefaultDept> dutyDepts=departmentDao.getDutyDeptsByQuotaCover(firstLevelChildQuotaCover.getId());
					DefaultDept dutyDept=null;
					for (DefaultDept defaultDept : dutyDepts) {
						dutyDept=defaultDept;
						break;
					}
					createQuotaItemCreator(quotaType,firstLevelChildQuotaCover,dutyDept,quotaItemCreator.getYear(),session);
				}	
				
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
	
	//���ݶ����ھ���ָ�����������������ӿھ�ָ��������
	@Expose
	public void createAllChildrenQuotaItemCreators(){		
		QuotaCover topQuotaCover=quotaCoverDao.getTopQuotaCovers().get(0);
		Collection<QuotaItemCreator> quotaItemCreators=getQuotaItemCreatorsByQuotaCover(topQuotaCover.getId());
		createChildrenQuotaItemCreators(quotaItemCreators);
	}
	
	//���ݶ����ھ���ָ�����������������ӿھ�ָ��������
	@Expose
	public void createChildrenQuotaItemCreators(Collection<QuotaItemCreator> quotaItemCreators){		
		Session session=this.getSessionFactory().openSession();
		try {
			for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
				QuotaType quotaType=quotaItemCreator.getQuotaType();
				QuotaCover quotaCover=quotaItemCreator.getQuotaCover();
				Collection<QuotaCover> childrenQuotaCoversTree=quotaCoverDao.getQuotaCoversTreeByFatherCover(quotaCover.getId(),new ArrayList<QuotaCover>());
				for (QuotaCover childQuotaCover : childrenQuotaCoversTree) {
					Collection<DefaultDept> dutyDepts=departmentDao.getDutyDeptsByQuotaCover(childQuotaCover.getId());
					DefaultDept dutyDept=null;
					for (DefaultDept defaultDept : dutyDepts) {
						dutyDept=defaultDept;
						break;
					}
					createQuotaItemCreator(quotaType,childQuotaCover,dutyDept,quotaItemCreator.getYear(),session);
				}	
				
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
	
	void createQuotaItemCreator(QuotaType quotaType,QuotaCover quotaCover,DefaultDept quotaDutyDept,int year,Session session){
		boolean isExists=false;
		String checkString="from "+QuotaItemCreator.class.getName()+" where quotaType.id='"+quotaType.getId()
				+"' and year="+year+" and quotaCover.id='"+quotaCover.getId()+"'";
		Collection<QuotaItemCreator> checkout=this.query(checkString);
		if (checkout.size()>0) {
			isExists=true;
		}
		if (!isExists) {
			QuotaItemCreator quotaItemCreator=new QuotaItemCreator();
			quotaItemCreator.setName(quotaType.getName());
			quotaItemCreator.setYear(year);
			quotaItemCreator.setQuotaType(quotaType);
			quotaItemCreator.setQuotaCover(quotaCover);
			quotaItemCreator.setQuotaDutyDept(quotaDutyDept);
			session.merge(quotaItemCreator);
			session.flush();
			session.clear();
		}
	}
	
	//����ָ�����������ɾ���ָ��
	@Expose
	public void createQuotaItems() throws Exception{
		Collection<QuotaItemCreator> quotaItemCreators;
		IUser loginUser = ContextHolder.getLoginUser();
		if (loginUser.isAdministrator()) {
			quotaItemCreators=getAll();
		}else {
			List<IDept> iDepts=loginUser.getDepts();
			quotaItemCreators=getQuotaItemCreatorsByManageDept(iDepts.get(0).getId());
		}
		
		Session session = this.getSessionFactory().openSession();
		
		Collection<QuotaItemCreator> quotaItemCreatorsWithoutQuotaItems=new ArrayList<QuotaItemCreator>();
		
		for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
			Collection<QuotaItem> quotaItems = quotaItemDao.getQuotaItemsByQuotaItemCreator(quotaItemCreator.getId());
			if (quotaItems.size() == 0) {
				quotaItemCreatorsWithoutQuotaItems.add(quotaItemCreator);
			}
		}
		
		try {
			for (QuotaItemCreator quotaItemCreator : quotaItemCreatorsWithoutQuotaItems) {	
				String rate = quotaItemCreator.getQuotaType().getRate();
				switch (rate) {
				case "��": {
					QuotaItem quotaItem = new QuotaItem();
					quotaItem.setYear(quotaItemCreator.getYear());
					quotaItem.setQuotaItemCreator(quotaItemCreator);
					session.save(quotaItem);
					session.flush();
					session.clear();
					break;
				}
				case "��": {
					Collection<QuotaPropertyValue> quotaPropertyValues = quotaPropertyValueDao.getQuotaPropertyValuesByQuotaItemCreator(quotaItemCreator.getId());
					int monthCount = 12;
					for (int i = 1; i <= monthCount; i++) {
						QuotaItem quotaItem = new QuotaItem();
						quotaItem.setYear(quotaItemCreator.getYear());
						quotaItem.setMonth(i);
						quotaItem.setQuotaItemCreator(quotaItemCreator);
						session.save(quotaItem);
						session.flush();
						session.clear();
						for (QuotaPropertyValue quotaPropertyValue : quotaPropertyValues) {
							QuotaTargetValue quotaTargetValue = new QuotaTargetValue();
							quotaTargetValue.setQuotaItem(quotaItem);
							quotaTargetValue.setQuotaProperty(quotaPropertyValue.getQuotaProperty());
							quotaTargetValue.setParameterName(quotaPropertyValue.getQuotaProperty().getParameterName()+"_M");
							session.save(quotaTargetValue);
							session.flush();//����Ϊ��session�����е����������ݿ�ͬ��
							session.clear();//���þ������session�����е����ݣ�����ʵ����ͻ��session������������ͬ��ʵ����
						}
					}
					break;
				}
				default:
					break;
				}
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}
	
	//����ȫ���������Ŀ��ֵ
	@Expose
	public void createQuotaPropertyValues() throws Exception{
		Session session=this.getSessionFactory().openSession();
		Collection<QuotaItemCreator> quotaItemCreators=getQuotaItemCreatorsWithoutQuotaPropertyValues();
		Collection<QuotaProperty> quotaProperties=quotaPropertyDao.getAll();
		try {
			for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
				for (QuotaProperty quotaProperty : quotaProperties) {
					QuotaPropertyValue quotaPropertyValue=new QuotaPropertyValue();
					quotaPropertyValue.setQuotaProperty(quotaProperty);
					quotaPropertyValue.setQuotaItemCreator(quotaItemCreator);
					quotaPropertyValue.setValue(88888888.0);
					session.save(quotaPropertyValue);
					session.flush();
					session.clear();
				}
				linkQuotaFormulas(quotaItemCreator.getId());
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			
		}
	}

	@DataResolver
	public void saveQuotaItemCreators(Collection<QuotaItemCreator> quotaItemCreators) {
		Session session = this.getSessionFactory().openSession();
		try {
			for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
				EntityState state = EntityUtils.getState(quotaItemCreator);
				QuotaType quotaType = quotaTypeDao.getQuotaType(quotaItemCreator.getQuotaType().getId());
				if (state.equals(EntityState.NEW)) {
					boolean isExists=false;
					String checkString="from "+QuotaItemCreator.class.getName()+" where quotaType.id='"+quotaType.getId()
							+"' and year="+quotaItemCreator.getYear()+" and quotaCover.id='"+quotaItemCreator.getQuotaCover().getId()+"'";
					Collection<QuotaItemCreator> checkout=this.query(checkString);
					if (checkout.size()>0) {
						isExists=true;
					}
					if (!isExists) {
						quotaItemCreator.setName(quotaType.getName());
						quotaItemCreator.setQuotaType(quotaType);
						quotaItemCreator.setQuotaCover(quotaCoverDao.getQuotaCover(quotaItemCreator.getQuotaCover().getId()));
						session.merge(quotaItemCreator);
						session.flush();
					}		
				} else if (state.equals(EntityState.MODIFIED)) {
					QuotaItemCreator thisQuotaItemCreator=getQuotaItemCreator(quotaItemCreator.getId());
					thisQuotaItemCreator.setQuotaType(quotaType);
					thisQuotaItemCreator.setName(quotaType.getName());
					thisQuotaItemCreator.setQuotaCover(quotaCoverDao.getQuotaCover(quotaItemCreator.getQuotaCover().getId()));
					thisQuotaItemCreator.setQuotaDutyDept(departmentDao.getDept(quotaItemCreator.getQuotaDutyDept().getId()));
					thisQuotaItemCreator.setYear(quotaItemCreator.getYear());
					session.merge(thisQuotaItemCreator);
				} else if (state.equals(EntityState.DELETED)) {
					IUser loginuser = ContextHolder.getLoginUser();
					if (loginuser == null) {
						throw new NoneLoginException("Please login first!");
					}else {
						boolean isAdmin=loginuser.isAdministrator();
						if (isAdmin) {
							//����ɾ��QuotaPropertyValue
							Collection<QuotaPropertyValue> quotaPropertyValues=quotaPropertyValueDao.getQuotaPropertyValuesByQuotaItemCreator(quotaItemCreator.getId());
							quotaPropertyValueDao.deleteQuotaPropertyValues(quotaPropertyValues);
							
							//����ɾ��QuotaItem
							Collection<QuotaItem> quotaItems=quotaItemDao.getQuotaItemsByQuotaItemCreator(quotaItemCreator.getId());
							quotaItemDao.deleteQuotaItems(quotaItems);
							
							quotaItemCreator.setQuotaCover(null);
							quotaItemCreator.setQuotaDutyDept(null);
							quotaItemCreator.setQuotaType(null);
							quotaItemCreator.setQuotaFormulas(null);
							session.delete(quotaItemCreator);
							session.flush();
							session.clear();
						}else {
							Collection<QuotaPropertyValue> quotaPropertyValues=quotaPropertyValueDao.getQuotaPropertyValuesByQuotaItemCreator(quotaItemCreator.getId());
							if (quotaPropertyValues.size()>0) {
								System.out.print("����ɾ��������ָ������Ŀ��ֵ");
							}else {
								Collection<QuotaItem> quotaItems=quotaItemDao.getQuotaItemsByQuotaItemCreator(quotaItemCreator.getId());
								if (quotaItems.size()>0) {
									System.out.print("����ɾ��������ָ��ľ���ָ���¼");
								}else {
									quotaItemCreator.setQuotaCover(null);
									quotaItemCreator.setQuotaDutyDept(null);
									quotaItemCreator.setQuotaType(null);
									quotaItemCreator.setQuotaFormulas(null);
									session.delete(quotaItemCreator);
									session.flush();
									session.clear();
								}
							}
						}
					}	
				}
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}
	
	//���������ָ��������
	@Expose
	public void copyQuotaItemCreatorsFromLastYear(){
		IUser loginuser = ContextHolder.getLoginUser();
		if (loginuser == null) {
			throw new NoneLoginException("Please login first!");
		}else {
			boolean isAdmin=loginuser.isAdministrator();
			if (isAdmin) {
				Calendar calendar=Calendar.getInstance();
				//��ȡִ��ʱ������
				int year=calendar.get(Calendar.YEAR);
				int month=calendar.get(Calendar.MONTH)+1;//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
				
				//�����2�¿�ʼ���ſ�ִ����һ���ָ�긴�Ʋ���
				if (month>1){
					Collection<QuotaItemCreator> quotaItemCreators=getQuotaItemCreatorsByYear(year-1);
					Session session=this.getSessionFactory().openSession();
					try {
						for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
							//����ɾ��QuotaItem
							Collection<QuotaItem> quotaItems=quotaItemDao.getQuotaItemsByQuotaItemCreator(quotaItemCreator.getId());
							quotaItemDao.deleteQuotaItems(quotaItems);
							
							quotaItemCreator.setYear(year);
							session.merge(quotaItemCreator);
							session.flush();
							session.clear();
						}
					} catch (Exception e) {
						System.out.print(e.toString());
					}finally{
						session.flush();
						session.clear();
					}
				}
			}else {
				System.out.print("�ǹ���ԱȨ�޲���ִ�д˲���");
			}
		}
	}
	
	@DataResolver
	public void deleteQuotaItemCreators(Collection<QuotaItemCreator> quotaItemCreators) {
		Session session = this.getSessionFactory().openSession();
		try {
			for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
				//����ɾ��QuotaPropertyValue
				Collection<QuotaPropertyValue> quotaPropertyValues=quotaPropertyValueDao.getQuotaPropertyValuesByQuotaItemCreator(quotaItemCreator.getId());
				quotaPropertyValueDao.deleteQuotaPropertyValues(quotaPropertyValues);
				
				//����ɾ��QuotaItem
				Collection<QuotaItem> quotaItems=quotaItemDao.getQuotaItemsByQuotaItemCreator(quotaItemCreator.getId());
				quotaItemDao.deleteQuotaItems(quotaItems);
				
				quotaItemCreator.setQuotaCover(null);
				quotaItemCreator.setQuotaDutyDept(null);
				quotaItemCreator.setQuotaType(null);
				quotaItemCreator.setQuotaFormulas(null);
				session.delete(quotaItemCreator);
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

	//��������ָ����������ֻ�������Ա����
	@Expose
	public void clearQuotaItemCreatorsLastYear(){
		IUser loginuser = ContextHolder.getLoginUser();
		if (loginuser == null) {
			throw new NoneLoginException("Please login first!");
		}else {
			boolean isAdmin=loginuser.isAdministrator();
			if (isAdmin) {
				Calendar calendar=Calendar.getInstance();
				//��ȡִ��ʱ������
				int year=calendar.get(Calendar.YEAR);
				int month=calendar.get(Calendar.MONTH)+1;//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
				
				//�����2�¿�ʼ���ſ�ִ����һ���ָ��ɾ������
				if (month>1){
					Collection<QuotaItemCreator> quotaItemCreators=getQuotaItemCreatorsByYear(year-1);
					deleteQuotaItemCreators(quotaItemCreators);
				}
			}else {
				System.out.print("�ǹ���ԱȨ�޲���ִ�д˲���");
			}
		}
	}
	
	//���ָ��������
	@Expose
	public void clearQuotaItemCreators() throws Exception{		
		Collection<QuotaItemCreator> quotaItemCreators;
		IUser loginUser = ContextHolder.getLoginUser();
		if (loginUser.isAdministrator()) {
			quotaItemCreators=getAll();
		}else {
			List<IDept> iDepts=loginUser.getDepts();
			quotaItemCreators=getQuotaItemCreatorsByManageDept(iDepts.get(0).getId());
		}
		
		if (loginUser == null) {
			throw new NoneLoginException("Please login first!");
		}else {
			deleteQuotaItemCreators(quotaItemCreators);
		}
	}
	
	//ͨ��ָ�����ཫ��ʽ����������ָ��������
	@Expose
	public void linkQuotaFormulas(String quotaItemCreatorId){
		Session session=this.getSessionFactory().openSession();
		try {
			QuotaItemCreator quotaItemCreator=getQuotaItemCreator(quotaItemCreatorId);
			QuotaType quotaType=quotaItemCreator.getQuotaType();
			Collection<QuotaTypeFormulaLink> quotaTypeFormulaLinks=quotaTypeFormulaLinkDao.getQuotaTypeFormulaLinksByQuotaType(quotaType.getId());
			
			Collection<QuotaPropertyValue> thisQuotaPropertyValues=quotaPropertyValueDao.getQuotaPropertyValuesByQuotaItemCreator(quotaItemCreator.getId());
			Set thisParameterSet=new HashSet();
			
			for (QuotaPropertyValue quotaPropertyValue : thisQuotaPropertyValues) {
				thisParameterSet.add(quotaPropertyValue.getQuotaProperty().getParameterName());
				if (quotaType.getRate().equals("��")) {
					thisParameterSet.add(quotaPropertyValue.getQuotaProperty().getParameterName()+"_M");
				}
			}
			
			quotaItemCreator.setQuotaFormulas(null);
			session.merge(quotaItemCreator);
			session.flush();
			session.clear();
			
			Set<QuotaFormula> formulas = new HashSet<QuotaFormula>();
			
			for (QuotaTypeFormulaLink quotaTypeFormulaLink : quotaTypeFormulaLinks) {
				Collection<FormulaParameter> formulaParameters=quotaTypeFormulaLink.getFormulaParameters();
				Set formulaLinkParameterSet=new HashSet();
				for (FormulaParameter formulaParameter : formulaParameters) {
					formulaLinkParameterSet.add(formulaParameter.getParameterName());
				}
				if (thisParameterSet.containsAll(formulaLinkParameterSet)) {
					formulas.add(quotaTypeFormulaLink.getQuotaFormula());
				}
			}
			
			quotaItemCreator.setQuotaFormulas(formulas);
			session.merge(quotaItemCreator);
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			session.flush();
			session.close();
		}
		
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
