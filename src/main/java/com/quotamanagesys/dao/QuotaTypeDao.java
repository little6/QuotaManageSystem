package com.quotamanagesys.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.model.DefaultUser;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.quotamanagesys.interceptor.ResultTableCreator;
import com.quotamanagesys.model.QuotaCover;
import com.quotamanagesys.model.QuotaItem;
import com.quotamanagesys.model.QuotaItemCreator;
import com.quotamanagesys.model.QuotaItemViewMap;
import com.quotamanagesys.model.QuotaLevel;
import com.quotamanagesys.model.QuotaPropertyValue;
import com.quotamanagesys.model.QuotaType;
import com.quotamanagesys.model.QuotaTypeFormulaLink;
import com.quotamanagesys.tools.CriteriaConvertCore;

@Component
public class QuotaTypeDao extends HibernateDao {
	
	@Resource
	QuotaLevelDao quotaLevelDao;
	@Resource
	QuotaTypeDao quotaTypeDao;
	@Resource
	QuotaItemCreatorDao quotaItemCreatorDao;
	@Resource
	QuotaPropertyValueDao quotaPropertyValueDao;
	@Resource
	QuotaItemDao quotaItemDao;
	@Resource
	QuotaCoverDao quotaCoverDao;
	@Resource
	DepartmentDao departmentDao;
	@Resource
	QuotaItemViewMapDao quotaItemViewMapDao;
	@Resource
	UserDao userDao;
	@Resource
	ResultTableCreator resultTableCreator;
	@Resource
	CriteriaConvertCore criteriaConvertCore;
	@Resource
	QuotaTypeFormulaLinkDao quotaTypeFormulaLinkDao;
	
	@DataProvider
	public Collection<QuotaType> getAll() {
		String hqlString = "from " + QuotaType.class.getName();
		Collection<QuotaType> quotaTypes = this.query(hqlString);
		return quotaTypes;
	}
	
	//��ҳ��ʽ��ѯ
	@DataProvider
	public void getQuotaTypesWithPage(Page<QuotaType> page,Criteria criteria) throws Exception{
		String filterString=criteriaConvertCore.convertToSQLString(criteria);
		if (!filterString.equals("")) {
			filterString=" and ("+filterString+")";
		}
		
		String hqlString = "from " + QuotaType.class.getName()
				+ " where rate like '%%'"+filterString
				+" order by quotaLevel.level asc,manageDept.name asc,rate asc,quotaDimension.name asc";
		this.pagingQuery(page, hqlString, "select count(*)" + hqlString);
	}
	
	@DataProvider
	public QuotaType getQuotaType(String id){
		String hqlString = "from " + QuotaType.class.getName()+" where id='"+id+"'";
		List<QuotaType> quotaTypes = this.query(hqlString);
		if (quotaTypes.size()>0) {
			return quotaTypes.get(0);
		}else {
			return null;
		}
	}
	
	@DataProvider
	public QuotaType getQuotaTypeByName(String quotaTypeName){
		String hqlString = "from " + QuotaType.class.getName()+" where name='"+quotaTypeName+"'";
		List<QuotaType> quotaTypes = this.query(hqlString);
		if (quotaTypes.size()>0) {
			return quotaTypes.get(0);
		}else {
			return null;
		}
	}
	
	@DataProvider
	public Collection<QuotaType> getQuotaTypesInUsed(){
		String hqlString = "from " + QuotaType.class.getName()+" where inUsed=true";
		Collection<QuotaType> quotaTypes = this.query(hqlString);
		return quotaTypes;
	}
	
	@DataProvider
	public Collection<QuotaType> getQuotaTypesInUsedByManageDept(String manageDeptId) {
		String hqlString="from "+QuotaType.class.getName()+" where inUsed=true and manageDept.id='"+manageDeptId+"'";
		Collection<QuotaType> quotaTypes=this.query(hqlString);
		return quotaTypes;
	}
	
	@DataProvider
	public Collection<QuotaType> getQuotaTypesInUsedManageDept(String manageDeptId){
		String hqlString = "from " + QuotaType.class.getName()+" where inUsed=true and manageDept.id='"+manageDeptId+"'";
		Collection<QuotaType> quotaTypes = this.query(hqlString);
		return quotaTypes;
	}
	
	@DataProvider
	public QuotaType getFatherQuotaType(String id){
		String hqlString = "from " + QuotaType.class.getName()+" where id='"+id+"'";
		List<QuotaType> quotaTypes = this.query(hqlString);
		if (quotaTypes.size()>0) {
			return quotaTypes.get(0).getFatherQuotaType();
		}else {
			return null;
		}
	}
	
	@DataProvider
	public Collection<QuotaType> getFatherQuotaTypesByQuotaLevel(String quotaLevelId) throws Exception{
		QuotaLevel quotaLevel=quotaLevelDao.getQuotaLevel(quotaLevelId);
		Session session = this.getSessionFactory().openSession();
		List<QuotaType> fatherQuotaTypes= session.createCriteria(QuotaType.class).createAlias("quotaLevel", "l")
				.add(Restrictions.eq("l.level",(quotaLevel.getLevel())-1)).list();
		//��ȡ��ָ������
		session.flush();
		session.close();
		return fatherQuotaTypes;
	}
	
	public void getFatherQuotaTypeTree(QuotaType quotaType,Collection<QuotaType> fatherQuotaTypeTree){
		QuotaType fatherQuotaType=quotaType.getFatherQuotaType();
		if (fatherQuotaType!=null) {
			fatherQuotaTypeTree.add(fatherQuotaType);
			getFatherQuotaTypeTree(fatherQuotaType, fatherQuotaTypeTree);
		}
	}
	
	@DataProvider
	public Collection<QuotaType> getTopLevelQuotaTypes(){
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
		String hqlString="from "+QuotaType.class.getName()+" where quotaLevel.level='"+highestLevel+"'";
		Collection<QuotaType> quotaTypes=this.query(hqlString);
		return quotaTypes;
	}
	
	@DataProvider
	public Collection<QuotaType> getChildrenQuotaTypes(String id){
		String hqlString="from "+QuotaType.class.getName()+" where fatherQuotaType.id='"+id+"'";
		Collection<QuotaType> quotaTypes=this.query(hqlString);
		return quotaTypes;
	}

	@DataProvider
	public Collection<QuotaType> getQuotaTypesByManageDept(String manageDeptId) {
		String hqlString="from "+QuotaType.class.getName()+" where manageDept.id='"+manageDeptId+"'";
		Collection<QuotaType> quotaTypes=this.query(hqlString);
		return quotaTypes;
	}

	@DataProvider
	public Collection<QuotaType> getQuotaTypesByProfession(String quotaProfessionId) {
		String hqlString="from "+QuotaType.class.getName()+" where quotaProfession.id='"+quotaProfessionId+"'";
		Collection<QuotaType> quotaTypes=this.query(hqlString);
		return quotaTypes;
	}
	
	@DataProvider
	public Collection<QuotaType> getQuotaTypesByQuotaLevel(String quotaLevelId) {
		String hqlString="from "+QuotaType.class.getName()+" where quotaLevel.id='"+quotaLevelId+"'";
		Collection<QuotaType> quotaTypes=this.query(hqlString);
		return quotaTypes;
	}
	
	@DataProvider
	public Collection<QuotaType> getQuotaTypesByQuotaDimension(String quotaDimensionId){
		String hqlString="from "+QuotaType.class.getName()+" where quotaDimension.id='"+quotaDimensionId+"'";
		Collection<QuotaType> quotaTypes=this.query(hqlString);
		return quotaTypes;
	}
	
	@DataProvider
	public Collection<QuotaType> getQuotaTypesByFatherQuotaType(String fatherQuotaTypeId){
		String hqlString="from "+QuotaType.class.getName()+" where fatherQuotaType.id='"+fatherQuotaTypeId+"'";
		Collection<QuotaType> quotaTypes=this.query(hqlString);
		return quotaTypes;
	}

	@DataResolver
	public void saveQuotaTypes(Collection<QuotaType> quotaTypes) {
		Session session = this.getSessionFactory().openSession();
		try {
			for (QuotaType quotaType : quotaTypes) {
				EntityState state = EntityUtils.getState(quotaType);
				if (state.equals(EntityState.NEW)) {
					session.save(quotaType);
				} else if (state.equals(EntityState.MODIFIED)) {
					boolean needToUpdate=false;
					QuotaType fatherQuotaType=quotaType.getFatherQuotaType();
					QuotaType oldQuotaType=getQuotaType(quotaType.getId());
					QuotaType oldFatherQuotaType=oldQuotaType.getFatherQuotaType();
					if (fatherQuotaType==null) {
						quotaType.setFatherQuotaType(oldFatherQuotaType);
					}else {
						quotaType.setFatherQuotaType(quotaTypeDao.getQuotaType(fatherQuotaType.getId()));
					}
					session.merge(quotaType);
					session.flush();
					session.clear();
					
					//ָ���������Ʊ��
					if (!quotaType.getName().equals(oldQuotaType.getName())) {
						needToUpdate=true;
						Collection<QuotaItemCreator> quotaItemCreators=quotaItemCreatorDao.getQuotaItemCreatorsByQuotaType(oldQuotaType.getId());
						if (quotaItemCreators.size()>0) {
							for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
								quotaItemCreator.setName(quotaType.getName());
								session.merge(quotaItemCreator);
								session.flush();	
							}
						}
					}
					
					//Ƶ�ʱ��
					if (!quotaType.getRate().equals(oldQuotaType.getRate())) {
						Collection<QuotaItemCreator> quotaItemCreators=quotaItemCreatorDao.getQuotaItemCreatorsByQuotaType(oldQuotaType.getId());
						if (quotaItemCreators.size()>0) {
							for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
								//Ƶ�ʱ����Ҫɾ��ԭ��quotaItemCreator������е�quotaItems
								Collection<QuotaItem> quotaItems=quotaItemDao.getQuotaItemsByQuotaItemCreator(quotaItemCreator.getId());
								quotaItemDao.deleteQuotaItems(quotaItems);
							}
						}
					}
					
					//�����ű��
					if(!quotaType.getManageDept().getId().equals(oldQuotaType.getManageDept().getId())){
						needToUpdate=true;
						Collection<QuotaItemCreator> quotaItemCreators=quotaItemCreatorDao.getQuotaItemCreatorsByQuotaType(oldQuotaType.getId());
						if (quotaItemCreators.size()>0) {
							QuotaCover topQuotaCover=quotaCoverDao.getTopQuotaCovers().get(0);
							for (QuotaItemCreator quotaItemCreator : quotaItemCreators) {
								if (quotaItemCreator.getQuotaCover().equals(topQuotaCover)) {
									quotaItemCreator.setQuotaDutyDept(departmentDao.getDept(quotaType.getManageDept().getId()));
									session.merge(quotaItemCreator);
									session.flush();
									session.clear();
								} else {
									continue;
								}
							}
						}
						
						//����״̬Ϊtrueʱ��ִ���¾ɲ���ָ����ʾ�������²���
						if (quotaType.isInUsed()==true) {
							//����ԭ���������û���ָ����ʾ����
							Collection<DefaultUser> oldUsers=userDao.getUsersByDept(oldQuotaType.getManageDept().getId());
							for (DefaultUser oldUser : oldUsers) {
								Collection<QuotaItemViewMap> quotaItemViewMaps=quotaItemViewMapDao.getQuotaItemViewMapsByUser(oldUser.getUsername());
								quotaItemViewMapDao.delete(quotaItemViewMaps);
							}
							//�����ֹ��������û���ָ����ʾ����
							Collection<DefaultUser> newUsers=userDao.getUsersByDept(quotaType.getManageDept().getId());
							for (DefaultUser newUser : newUsers) {
								quotaItemViewMapDao.initQuotaItemViewMapsByUser(newUser.getUsername());
							}
						}
					}
					
					//����״̬���
					if (quotaType.isInUsed()!=oldQuotaType.isInUsed()) {
						if (quotaType.isInUsed()==false) {
							Collection<QuotaItemCreator> quotaItemCreators=quotaItemCreatorDao.getQuotaItemCreatorsByQuotaType(oldQuotaType.getId());
							if (quotaItemCreators.size()>0) {
								quotaItemCreatorDao.deleteQuotaItemCreators(quotaItemCreators);
							}
						}
					}
					
					if (needToUpdate==true) {
						Collection<QuotaItem> quotaItems=quotaItemDao.getQuotaItemsByQuotaType(quotaType.getId());
						resultTableCreator.createOrUpdateResultTable(quotaItems);
					}
				} else if (state.equals(EntityState.DELETED)) {
					//���¼�ָ������ĸ�������Ϊnull
					Collection<QuotaType> childrenQuotaTypes=getChildrenQuotaTypes(quotaType.getId());
					for (QuotaType child : childrenQuotaTypes) {
						child.setFatherQuotaType(null);
						session.merge(child);
						session.flush();
						session.clear();
					}
					
					//����ɾ��QuotaTypeFormulaLink
					quotaTypeFormulaLinkDao.clearQuotaTypeFormulaLink(quotaType);
					
					//����ɾ��QuotaItemCreator
					Collection<QuotaItemCreator> quotaItemCreators=quotaItemCreatorDao.getQuotaItemCreatorsByQuotaType(quotaType.getId());
					quotaItemCreatorDao.deleteQuotaItemCreators(quotaItemCreators);
					
					//����ɾ��QuotaPropertyValue
					Collection<QuotaPropertyValue> quotaPropertyValues=quotaPropertyValueDao.getQuotaPropertyValuesByQuotaProperty(quotaType.getId());
					quotaPropertyValueDao.deleteQuotaPropertyValues(quotaPropertyValues);
	
					quotaType.setFatherQuotaType(null);
					quotaType.setManageDept(null);
					quotaType.setQuotaDimension(null);
					quotaType.setQuotaLevel(null);
					quotaType.setQuotaProfession(null);
					quotaType.setQuotaUnit(null);
					session.delete(quotaType);
					session.flush();
					session.clear();
				}
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
	
	@DataResolver
	public void deleteQuotaTypes(Collection<QuotaType> quotaTypes) {
		Session session = this.getSessionFactory().openSession();
		quotaTypeFormulaLinkDao.clearQuotaTypesFormulaLink(quotaTypes);
		try {
			for (QuotaType quotaType : quotaTypes) {
				//���¼�ָ������ĸ�������Ϊnull
				Collection<QuotaType> childrenQuotaTypes=getChildrenQuotaTypes(quotaType.getId());
				for (QuotaType child : childrenQuotaTypes) {
					child.setFatherQuotaType(null);
					session.merge(child);
					session.flush();
					session.clear();
				}
				
				//����ɾ��QuotaItemCreator
				Collection<QuotaItemCreator> quotaItemCreators=quotaItemCreatorDao.getQuotaItemCreatorsByQuotaType(quotaType.getId());
				if (quotaItemCreators.size()>0) {
					quotaItemCreatorDao.deleteQuotaItemCreators(quotaItemCreators);
				}	
				
				//����ɾ��QuotaPropertyValue
				Collection<QuotaPropertyValue> quotaPropertyValues=quotaPropertyValueDao.getQuotaPropertyValuesByQuotaProperty(quotaType.getId());
				if (quotaPropertyValues.size()>0) {
					quotaPropertyValueDao.deleteQuotaPropertyValues(quotaPropertyValues);
				}
				
				quotaType.setFatherQuotaType(null);
				quotaType.setManageDept(null);
				quotaType.setQuotaDimension(null);
				quotaType.setQuotaLevel(null);
				quotaType.setQuotaProfession(null);
				quotaType.setQuotaUnit(null);
				session.delete(quotaType);
				session.flush();
				session.clear();
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
	
	@Expose
	public void clearWrongQuotaTypes(){
		String hqlString="from "+QuotaType.class.getName()+" where "
				+"quotaProfession.id=null "
				+"or quotaLevel.id=null "
				+"or quotaDimension.id=null "
				+"or quotaUnit.id=null "
				+"or manageDept.id=null "
				+"or rate=null ";
		Collection<QuotaType> wrongQuotaTypes=this.query(hqlString);
		if (wrongQuotaTypes.size()>0) {
			System.out.println("wrong quotaTypes size is :"+wrongQuotaTypes.size());
			deleteQuotaTypes(wrongQuotaTypes);
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
