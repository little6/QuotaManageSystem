package com.quotamanagesys.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.quotamanagesys.model.FormulaParameter;
import com.quotamanagesys.model.QuotaFormula;
import com.quotamanagesys.model.QuotaType;
import com.quotamanagesys.model.QuotaTypeFormulaLink;

@Component
public class QuotaTypeFormulaLinkDao extends HibernateDao {

	@Resource
	QuotaTypeDao quotaTypeDao;
	@Resource
	QuotaFormulaDao quotaFormulaDao;
	@Resource
	FormulaParameterDao formulaParameterDao;

	@DataProvider
	public Collection<QuotaTypeFormulaLink> getAll() {
		String hqlString = "from " + QuotaTypeFormulaLink.class.getName();
		Collection<QuotaTypeFormulaLink> quotaTypeFormulaLinks = this
				.query(hqlString);
		return quotaTypeFormulaLinks;
	}

	@DataProvider
	public Collection<QuotaTypeFormulaLink> getQuotaTypeFormulaLinksByQuotaType(
			String quotaTypeId) {
		String hqlString = "from " + QuotaTypeFormulaLink.class.getName()
				+ " where quotaType.id='" + quotaTypeId + "'";
		Collection<QuotaTypeFormulaLink> quotaTypeFormulaLinks = this
				.query(hqlString);
		return quotaTypeFormulaLinks;
	}
	
	

	@DataProvider
	public Collection<QuotaTypeFormulaLink> getQuotaTypeFormulaLinksByFormula(
			String quotaFormulaId) {
		String hqlString = "from " + QuotaTypeFormulaLink.class.getName()
				+ " where quotaFormula.id='" + quotaFormulaId + "'";
		Collection<QuotaTypeFormulaLink> quotaTypeFormulaLinks = this
				.query(hqlString);
		return quotaTypeFormulaLinks;
	}

	@DataProvider
	public QuotaTypeFormulaLink getQuotaTypeFormulaLink(String id) {
		String hqlString = "from " + QuotaTypeFormulaLink.class.getName()
				+ " where id='" + id + "'";
		List<QuotaTypeFormulaLink> quotaTypeFormulaLinks = this
				.query(hqlString);
		if (quotaTypeFormulaLinks.size() > 0) {
			return quotaTypeFormulaLinks.get(0);
		} else {
			return null;
		}
	}
	
	//��ȡ��ָ�����๫ʽ������¼�����Ĳ�������
	@DataProvider
	public Collection<FormulaParameter> getFormulaParametersByQuotaTypeFormulaLink(String quotaTypeId,String quotaFormulaId){
		if (quotaTypeId!=null&&quotaFormulaId!=null) {
			String hqlString="from "+QuotaTypeFormulaLink.class.getName()+" where quotaType.id='"+quotaTypeId+"'"
					+" and quotaFormula.id='"+quotaFormulaId+"'";
			List<QuotaTypeFormulaLink> quotaTypeFormulaLinks=this.query(hqlString);
			if (quotaTypeFormulaLinks.size()>0) {
				return quotaTypeFormulaLinks.get(0).getFormulaParameters();
			}else {
				return null;
			}
		}else {
			return null;
		}
	}

	//��������ָ�����ൽ��ʽ
	@Expose
	public void creatorQuotaTypeFormulaLinks(Collection<QuotaType> quotaTypes,
			Collection<FormulaParameter> formulaParameters,
			String quotaFormulaId) {
		Session session = this.getSessionFactory().openSession();
		Set<FormulaParameter> formulaParametersSet = new HashSet<FormulaParameter>();
		for (FormulaParameter formulaParameter : formulaParameters) {
			formulaParametersSet.add(formulaParameter);
		}

		try {
			for (QuotaType quotaType : quotaTypes) {
				QuotaFormula quotaFormula = quotaFormulaDao
						.getQuotaFormula(quotaFormulaId);
				String checkIsExsits = "from "
						+ QuotaTypeFormulaLink.class.getName()
						+ " where quotaType.id='" + quotaType.getId()
						+ "' and quotaFormula.quotaFormulaResult.id='"
						+ quotaFormula.getQuotaFormulaResult().getId() + "'";
				Collection<QuotaTypeFormulaLink> linkedTypeFormulaLinks = this.query(checkIsExsits);
				//���Ѵ���ͬ�๫ʽ�����������ִ�С�
				if (linkedTypeFormulaLinks.size() == 0) {
					QuotaType thisQuotaType = quotaTypeDao.getQuotaType(quotaType.getId());

					QuotaTypeFormulaLink quotaTypeFormulaLink = new QuotaTypeFormulaLink();
					quotaTypeFormulaLink.setQuotaType(thisQuotaType);
					quotaTypeFormulaLink.setQuotaFormula(quotaFormula);
					quotaTypeFormulaLink.setFormulaParameters(formulaParametersSet);

					session.save(quotaTypeFormulaLink);
					session.flush();
					session.clear();
				}
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}
	
	//���ָ����������ļ��㹫ʽ
	@Expose
	public void clearQuotaTypesFormulaLink(Collection<QuotaType> quotaTypes){
		for (QuotaType quotaType : quotaTypes) {
			clearQuotaTypeFormulaLink(quotaType);
		}
	}
	
	//���ָ����������ļ��㹫ʽ
	@Expose
	public void clearQuotaTypeFormulaLink(QuotaType quotaType){
		Session session=this.getSessionFactory().openSession();
		try {
			Collection<QuotaTypeFormulaLink> quotaTypeFormulaLinks=getQuotaTypeFormulaLinksByQuotaType(quotaType.getId());
			for (QuotaTypeFormulaLink quotaTypeFormulaLink : quotaTypeFormulaLinks) {
				quotaTypeFormulaLink.setQuotaFormula(null);
				quotaTypeFormulaLink.setQuotaType(null);
				quotaTypeFormulaLink.setFormulaParameters(null);
				session.delete(quotaTypeFormulaLink);
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
	
	//ָ�����๫ʽ����ά��������ά����
	@DataResolver
	public void saveQuotaTypeFormulaLink(String quotaTypeId,Collection<QuotaFormula> quotaFormulas){
		Session session=this.getSessionFactory().openSession();
		QuotaType quotaType=quotaTypeDao.getQuotaType(quotaTypeId);
		try {
			for (QuotaFormula quotaFormula : quotaFormulas) {
				EntityState state=EntityUtils.getState(quotaFormula);
				if ((state.equals(EntityState.NEW))||(state.equals(EntityState.NONE))) {
					String checkIsExsits = "from "+ QuotaTypeFormulaLink.class.getName()
							+ " where quotaType.id='" + quotaTypeId
							+ "' and quotaFormula.quotaFormulaResult.id='"
							+ quotaFormula.getQuotaFormulaResult().getId() + "'";
					Collection<QuotaTypeFormulaLink> linkedTypeFormulaLinks = this.query(checkIsExsits);
					if (linkedTypeFormulaLinks.size()==0) {
						QuotaTypeFormulaLink quotaTypeFormulaLink=new QuotaTypeFormulaLink();
						quotaTypeFormulaLink.setQuotaType(quotaType);
						QuotaFormula thisQuotaFormula=quotaFormulaDao.getQuotaFormula(quotaFormula.getId());
						quotaTypeFormulaLink.setQuotaFormula(thisQuotaFormula);
						
						session.save(quotaTypeFormulaLink);
						session.flush();
						session.clear();
					}else {
						for (QuotaTypeFormulaLink quotaTypeFormulaLink : linkedTypeFormulaLinks) {
							quotaTypeFormulaLink.setQuotaFormula(null);
							quotaTypeFormulaLink.setQuotaType(null);
							quotaTypeFormulaLink.setFormulaParameters(null);
							session.delete(quotaTypeFormulaLink);
							session.flush();
							session.clear();
							
							QuotaTypeFormulaLink newQuotaTypeFormulaLink=new QuotaTypeFormulaLink();
							newQuotaTypeFormulaLink.setQuotaType(quotaType);
							QuotaFormula thisQuotaFormula=quotaFormulaDao.getQuotaFormula(quotaFormula.getId());
							newQuotaTypeFormulaLink.setQuotaFormula(thisQuotaFormula);
							session.save(newQuotaTypeFormulaLink);
							session.flush();
							session.clear();
						}
					}
				}else if (state.equals(EntityState.DELETED)) {
					Collection<QuotaTypeFormulaLink> quotaTypeFormulaLinks=getQuotaTypeFormulaLinksByQuotaType(quotaTypeId);
					for (QuotaTypeFormulaLink quotaTypeFormulaLink : quotaTypeFormulaLinks) {
						if ((quotaTypeFormulaLink.getQuotaFormula().getId()).equals(quotaFormula.getId())) {
							quotaTypeFormulaLink.setQuotaFormula(null);
							quotaTypeFormulaLink.setQuotaType(null);
							quotaTypeFormulaLink.setFormulaParameters(null);
							session.delete(quotaTypeFormulaLink);
							session.flush();
							session.clear();
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
	
	//��CollectionΪǰ̨UpdateAction�������Ĳ���ʱ�����Ҫ��֤updateItem�е�alias�͸ú����еĲ�����һ��
	@DataResolver
	public void saveQuotaFormulaLinkParameters(Collection<FormulaParameter> formulaParameters,String quotaTypeId,String quotaFormulaId){
		Session session=this.getSessionFactory().openSession();
		String hqlString="from "+QuotaTypeFormulaLink.class.getName()+" where quotaType.id='"+quotaTypeId+"'"
				+" and quotaFormula.id='"+quotaFormulaId+"'";
		List<QuotaTypeFormulaLink> quotaTypeFormulaLinks=this.query(hqlString);
		
		if (quotaTypeFormulaLinks.size()>0) {
			QuotaTypeFormulaLink quotaTypeFormulaLink=quotaTypeFormulaLinks.get(0);
			Set<FormulaParameter> thisFormulaParameters=quotaTypeFormulaLink.getFormulaParameters();
			
			try {
				for (FormulaParameter formulaParameter : formulaParameters) {
					EntityState state=EntityUtils.getState(formulaParameter);
					if ((state.equals(EntityState.NEW))||(state.equals(EntityState.NONE))) {
						FormulaParameter thisFormulaParameter=formulaParameterDao.getFormulaParameter(formulaParameter.getId());
						thisFormulaParameters.add(thisFormulaParameter);
					}else if (state.equals(EntityState.DELETED)) {
						for (FormulaParameter formulaParameter2 : thisFormulaParameters) {
							if ((formulaParameter2.getId()).equals(formulaParameter.getId())) {
								thisFormulaParameters.remove(formulaParameter2);
								break;
							}
						}
					}
				}
				quotaTypeFormulaLink.setFormulaParameters(thisFormulaParameters);
				session.merge(quotaTypeFormulaLink);
			} catch (Exception e) {
				System.out.print(e.toString());
			}finally{
				session.flush();
				session.close();
			}
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
