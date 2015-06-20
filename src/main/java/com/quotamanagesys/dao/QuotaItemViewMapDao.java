package com.quotamanagesys.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.model.DefaultDept;
import com.bstek.bdf2.core.model.DefaultUser;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.quotamanagesys.model.QuotaItem;
import com.quotamanagesys.model.QuotaItemCreator;
import com.quotamanagesys.model.QuotaItemViewMap;
import com.quotamanagesys.tools.CriteriaConvertCore;

@Component
public class QuotaItemViewMapDao extends HibernateDao {

	@Resource
	DepartmentDao departmentDao;
	@Resource
	QuotaTypeDao quotaTypeDao;
	@Resource
	QuotaItemCreatorDao quotaItemCreatorDao;
	@Resource
	UserDao userDao;
	@Resource
	CriteriaConvertCore criteriaConvertCore;
	
	@DataProvider
	public Collection<QuotaItemViewMap> getAll(){
		String hqlString="from "+QuotaItemViewMap.class.getName();
		Collection<QuotaItemViewMap> quotaItemViewMaps=this.query(hqlString);
		return quotaItemViewMaps;
	}
	
	@DataProvider
	public Collection<QuotaItemViewMap> getQuotaItemViewMapsByUser(String userId){
		String hqlString="from "+QuotaItemViewMap.class.getName()+" where user.username='"+userId+"'";
		String orderByString=" order by quotaType.name asc,quotaType.quotaLevel.level asc,quotaType.manageDept.name,quotaCover.sort asc";
		hqlString=hqlString+orderByString;
		Collection<QuotaItemViewMap> quotaItemViewMaps=this.query(hqlString);
		return quotaItemViewMaps;
	}
	
	@DataProvider
	public void getQuotaItemViewMapsByUserWithPage(Page<QuotaItem> page,Criteria criteria,String userId) throws Exception{
		if (userId!=null) {
			String filterString=criteriaConvertCore.convertToSQLString(criteria);
			if (!filterString.equals("")) {
				filterString=" and ("+filterString+")";
			}
			
			String hqlString="from "+QuotaItemViewMap.class.getName()+" where user.username='"+userId+"'"+filterString;
			String orderByString=" order by quotaType.name asc,quotaType.quotaLevel.level asc,quotaType.manageDept.name,quotaCover.sort asc";
			hqlString=hqlString+orderByString;
			this.pagingQuery(page, hqlString, "select count(*)" + hqlString);
		} else {
			System.out.print("参数为空");
		}
	}
	
	@DataProvider
	public Collection<QuotaItemViewMap> getQuotaItemViewMapsByQuotaType(String quotaTypeId){
		String hqlString="from "+QuotaItemViewMap.class.getName()+" where quotaType.id='"+quotaTypeId+"'";
		Collection<QuotaItemViewMap> quotaItemViewMaps=this.query(hqlString);
		return quotaItemViewMaps;
	}
	
	@DataProvider
	public Collection<QuotaItemViewMap> getQuotaItemViewMapsByCover(String quotaCoverId){
		String hqlString="from "+QuotaItemViewMap.class.getName()+" where quotaCover.id='"+quotaCoverId+"'";
		Collection<QuotaItemViewMap> quotaItemViewMaps=this.query(hqlString);
		return quotaItemViewMaps;
	}
	
	@DataProvider
	public Collection<QuotaItemViewMap> getQuotaItemViewMapsByQuotaTypeAndCover(String quotaTypeId,String quotaCoverId){
		String hqlString="from "+QuotaItemViewMap.class.getName()+" where quotaType.id='"+quotaTypeId+"' and quotaCover.id='"+quotaCoverId+"'";
		Collection<QuotaItemViewMap> quotaItemViewMaps=this.query(hqlString);
		return quotaItemViewMaps;
	}
	
	@DataProvider
	public QuotaItemViewMap getQuotaItemViewMapByQuotaTypeAndCover(String userId,String quotaTypeId,String quotaCoverId){
		String hqlString="from "+QuotaItemViewMap.class.getName()+" where user.username='"+userId+"' and quotaType.id='"+quotaTypeId+"' and quotaCover.id='"+quotaCoverId+"'";
		List<QuotaItemViewMap> quotaItemViewMaps=this.query(hqlString);
		if (quotaItemViewMaps.size()>0) {
			return quotaItemViewMaps.get(0);
		} else {
			return null;
		}
	}
	
	@DataProvider
	public QuotaItemViewMap getQuotaItemViewMap(String id){
		String hqlString="from "+QuotaItemViewMap.class.getName()+" where id='"+id+"'";
		List<QuotaItemViewMap> quotaItemViewMaps=this.query(hqlString);
		if (quotaItemViewMaps.size()>0) {
			return quotaItemViewMaps.get(0);
		} else {
			return null;
		}
	}
	
	@DataProvider
	public Collection<QuotaItemCreator> getQuotaItemCreatorsNotYetMapByUser(String userId){
		Collection<QuotaItemCreator> quotaItemCreators=quotaItemCreatorDao.getAll();
		Collection<QuotaItemViewMap> quotaItemViewMaps=getQuotaItemViewMapsByUser(userId);
		
		for (QuotaItemViewMap quotaItemViewMap : quotaItemViewMaps) {
			String quotaTypeId=quotaItemViewMap.getQuotaType().getId();
			String quotaCoverId=quotaItemViewMap.getQuotaCover().getId();
			QuotaItemCreator quotaItemCreator=quotaItemCreatorDao.getQuotaItemCreatorByQuotaTypeAndCover(quotaTypeId, quotaCoverId);
			if (quotaItemCreator!=null) {
				for (QuotaItemCreator quotaItemCreator2 : quotaItemCreators) {
					if (quotaItemCreator2.getId().equals(quotaItemCreator.getId())) {
						quotaItemCreators.remove(quotaItemCreator2);
						break;
					}
				}
			}
		}
		
		return quotaItemCreators;
	}
	
	@DataProvider
	public void getQuotaItemCreatorsNotYetMapByUserWithPage(Page<QuotaItemCreator> page,Criteria criteria,String userId){
		if (page!=null) {
			ArrayList<QuotaItemCreator> results=new ArrayList<QuotaItemCreator>();
					
			int pageSize=page.getPageSize();
			int pageNo=page.getPageNo();
			
			int firstIndex=(pageNo-1)*pageSize;
			int lastIndex=pageNo*pageSize;
			
			Collection<QuotaItemCreator> quotaItemCreators=quotaItemCreatorDao.getQuotaItemCreatorsByCriteria(criteria);
			Collection<QuotaItemViewMap> quotaItemViewMaps=getQuotaItemViewMapsByUser(userId);
			
			for (QuotaItemViewMap quotaItemViewMap : quotaItemViewMaps) {
				String quotaTypeId=quotaItemViewMap.getQuotaType().getId();
				String quotaCoverId=quotaItemViewMap.getQuotaCover().getId();
				QuotaItemCreator quotaItemCreator=quotaItemCreatorDao.getQuotaItemCreatorByQuotaTypeAndCover(quotaTypeId, quotaCoverId);
				if (quotaItemCreator!=null) {
					for (QuotaItemCreator quotaItemCreator2 : quotaItemCreators) {
						if (quotaItemCreator2.getId().equals(quotaItemCreator.getId())) {
							quotaItemCreators.remove(quotaItemCreator2);
							break;
						}
					}
				}
			}

			int i=0;
			int j=0;
			for (QuotaItemCreator result : quotaItemCreators) {
				if (i<firstIndex) {
					i++;
					j++;
				}else {
					i=firstIndex;
					if (j<lastIndex) {
						results.add(result);
						j++;
					}
				}
			}
			
			page.setEntities(results);
			page.setEntityCount(quotaItemCreators.size());
		}else {
			System.out.print("page为空");
		}
	}
	
	@Expose
	public void initQuotaItemViewMapsByAll(){
		Collection<DefaultDept> depts=departmentDao.getAll();
		for (DefaultDept dept : depts) {
			initQuotaItemViewMapsByDept(dept.getId());
		}
	}
	
	@Expose
	public void initQuotaItemViewMapsByDept(String deptId){
		Session session = this.getSessionFactory().openSession();
		Collection<DefaultUser> users=userDao.getUsersByDept(deptId);
		
		try {
			for (DefaultUser user : users) {
				initQuotaItemViewMapsByUser(user.getUsername());
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
	
	@Expose
	public void initQuotaItemViewMapsByUser(String userId){
		Session session = this.getSessionFactory().openSession();
		DefaultUser user=userDao.getUser(userId);
		
		if (user!=null) {
			DefaultDept dept=userDao.getUserDept(userId);
					
			Collection<QuotaItemViewMap> quotaItemViewMaps=getQuotaItemViewMapsByUser(user.getUsername());
			if (quotaItemViewMaps.size()>0) {
				delete(quotaItemViewMaps);
			}
			
			if (dept!=null) {
				String deptId=dept.getId();
				try {
					List<QuotaItemCreator> deptLinkedQuotaItemCreators=(List<QuotaItemCreator>) quotaItemCreatorDao.getQuotaItemCreatorsByManageDeptOrDutyDept(deptId);
					
					int size=deptLinkedQuotaItemCreators.size();
					int index=0;
					
					while (index<=size-1) {
						QuotaItemCreator quotaItemCreator=deptLinkedQuotaItemCreators.get(index);
						QuotaItemViewMap quotaItemViewMap=new QuotaItemViewMap();
						quotaItemViewMap.setUser(user);
						quotaItemViewMap.setQuotaType(quotaItemCreator.getQuotaType());
						quotaItemViewMap.setQuotaCover(quotaItemCreator.getQuotaCover());
						quotaItemViewMap.setCanView(true);
						quotaItemViewMap.setDefaultView(true);
						session.merge(quotaItemViewMap);
						session.flush();
						index++;
					}
				} catch (Exception e) {
					System.out.print(e.toString());
				}finally{
					session.flush();
					session.close();
				}
			}else {
				System.out.print("用户未关联部门，无法初始化 ");
			}
		}else {
			System.out.print("用户为空，无法初始化指标可视关系");
		}
	}
	
	@Expose
	public void initQuotaItemViewMapsByQuotaItemCreator(String quotaItemCreatorId,boolean isDutyDeptChanged,boolean isCoverChanged){
		Session session = this.getSessionFactory().openSession();
		QuotaItemCreator quotaItemCreator=quotaItemCreatorDao.getQuotaItemCreator(quotaItemCreatorId);
		
		if (quotaItemCreator!=null) {
			String quotaTypeId=quotaItemCreator.getQuotaType().getId();
			String quotaCoverId=quotaItemCreator.getQuotaCover().getId();
			Collection<QuotaItemViewMap> quotaItemViewMaps=getQuotaItemViewMapsByQuotaTypeAndCover(quotaTypeId, quotaCoverId);
	
			try {
				if (isDutyDeptChanged==true) {
					if (quotaItemViewMaps.size()>0) {
						delete(quotaItemViewMaps);
					}
					
					DefaultDept manageDept=quotaItemCreator.getQuotaType().getManageDept();
					DefaultDept dutyDept=quotaItemCreator.getQuotaDutyDept();
					
					Collection<DefaultUser> users=userDao.getUsersByDept(manageDept.getId());
					
					if(!manageDept.getId().equals(dutyDept.getId())){
						users.addAll(userDao.getUsersByDept(dutyDept.getId()));
					}
					
					for (DefaultUser user : users) {
						QuotaItemViewMap quotaItemViewMap=new QuotaItemViewMap();
						quotaItemViewMap.setUser(user);
						quotaItemViewMap.setQuotaType(quotaItemCreator.getQuotaType());
						quotaItemViewMap.setQuotaCover(quotaItemCreator.getQuotaCover());
						quotaItemViewMap.setCanView(true);
						quotaItemViewMap.setDefaultView(true);
						session.save(quotaItemViewMap);
						session.flush();
					}
				}else {
					if (isCoverChanged) {
						for (QuotaItemViewMap quotaItemViewMap : quotaItemViewMaps) {
							QuotaItemViewMap thisQuotaItemViewMap=getQuotaItemViewMap(quotaItemViewMap.getId());
							thisQuotaItemViewMap.setQuotaCover(quotaItemCreator.getQuotaCover());
							session.merge(thisQuotaItemViewMap);
							session.flush();
							session.clear();
						}
					}	
				}
			} catch (Exception e) {
				System.out.print(e.toString());
			}finally{
				session.flush();
				session.close();
			}
			
		} else {
			System.out.print("指标生成器为空，无法初始化指标可视关系");
		}
	}
	
	@DataResolver
	public void saveQuotaItemViewMaps(Collection<QuotaItemViewMap> quotaItemViewMaps){
		Collection<QuotaItemViewMap> adds=new ArrayList<QuotaItemViewMap>();
		Collection<QuotaItemViewMap> updates=new ArrayList<QuotaItemViewMap>();
		Collection<QuotaItemViewMap> deletes=new ArrayList<QuotaItemViewMap>();
		
		for (QuotaItemViewMap quotaItemViewMap : quotaItemViewMaps) {
			EntityState state=EntityUtils.getState(quotaItemViewMap);
			if (state.equals(EntityState.NEW)) {
				adds.add(quotaItemViewMap);
			}else if (state.equals(EntityState.MODIFIED)) {
				updates.add(quotaItemViewMap);
			}else if (state.equals(EntityState.DELETED)) {
				deletes.add(quotaItemViewMap);
			}
		}
		if (adds.size()>0) {
			add(adds);
		}
		if (updates.size()>0) {
			update(updates);
		}
		if (deletes.size()>0) {
			delete(deletes);
		}
	}
	
	@Expose
	public void add(Collection<QuotaItemViewMap> quotaItemViewMaps){
		Session session=this.getSessionFactory().openSession();
		try {
			for (QuotaItemViewMap quotaItemViewMap : quotaItemViewMaps) {
				session.save(quotaItemViewMap);
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
	public void update(Collection<QuotaItemViewMap> quotaItemViewMaps){
		Session session=this.getSessionFactory().openSession();
		try {
			for (QuotaItemViewMap quotaItemViewMap : quotaItemViewMaps) {
				QuotaItemViewMap thisQuotaItemViewMap=getQuotaItemViewMap(quotaItemViewMap.getId());
				
				boolean canView=quotaItemViewMap.isCanView();
				thisQuotaItemViewMap.setCanView(canView);
				if (canView==false) {
					boolean defaultView=false;
					thisQuotaItemViewMap.setDefaultView(defaultView);
				}
				
				boolean defaultView=quotaItemViewMap.isDefaultView();
				if (canView==true) {
					thisQuotaItemViewMap.setDefaultView(defaultView);
				}
				
				session.merge(thisQuotaItemViewMap);
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
	public void delete(Collection<QuotaItemViewMap> quotaItemViewMaps){
		Session session=this.getSessionFactory().openSession();
		try {
			for (QuotaItemViewMap quotaItemViewMap : quotaItemViewMaps) {
				QuotaItemViewMap thisQuotaItemViewMap=getQuotaItemViewMap(quotaItemViewMap.getId());
				thisQuotaItemViewMap.setUser(null);
				thisQuotaItemViewMap.setQuotaType(null);
				thisQuotaItemViewMap.setQuotaCover(null);
				session.delete(thisQuotaItemViewMap);
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
