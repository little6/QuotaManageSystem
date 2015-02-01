package com.quotamanagesys.interceptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.nfunk.jep.JEP;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.Expose;
import com.quotamanagesys.dao.QuotaFormulaResultValueDao;
import com.quotamanagesys.dao.QuotaItemDao;
import com.quotamanagesys.dao.QuotaPropertyValueDao;
import com.quotamanagesys.dao.QuotaTargetValueDao;
import com.quotamanagesys.model.QuotaFormula;
import com.quotamanagesys.model.QuotaFormulaResultValue;
import com.quotamanagesys.model.QuotaItem;
import com.quotamanagesys.model.QuotaItemCreator;
import com.quotamanagesys.model.QuotaPropertyValue;
import com.quotamanagesys.model.QuotaTargetValue;
import com.quotamanagesys.model.QuotaType;

@Component
public class CalculateCore extends HibernateDao{
	@Resource
	QuotaItemDao quotaItemDao;
	@Resource
	QuotaPropertyValueDao quotaPropertyValueDao;
	@Resource
	QuotaFormulaResultValueDao quotaFormulaResultValueDao;
	@Resource
	QuotaTargetValueDao quotaTargetValueDao;
	
	//���¶�ȫ������ָ����м���
	@Expose
	public void calculateAll(){
		Collection<QuotaItem> quotaItems=quotaItemDao.getAll();
		calculate(quotaItems);
	}
	
	//�Ծ���ָ�꼯�Ͻ��м���
	@Expose
	public void calculate(Collection<QuotaItem> quotaItems){
		for (QuotaItem quotaItem : quotaItems) {
			//����þ���ָ���ԭ������
			quotaFormulaResultValueDao.excuteHQL("delete from "+QuotaFormulaResultValue.class.getName()
					+" where quotaItem.id='"+quotaItem.getId()+"'");
			//�Ծ���ָ����м���
			calculateQuotaItemResultValue(quotaItem);
		}
	}
	
	//�Ծ���ָ����м���
	public void calculateQuotaItemResultValue(QuotaItem quotaItem){
		QuotaItemCreator quotaItemCreator=quotaItem.getQuotaItemCreator();
		QuotaType quotaType=quotaItemCreator.getQuotaType();
		int digit=quotaType.getDigit();
		//����������
		JEP jep=new JEP();
		Set<QuotaFormula> quotaFormulas=quotaItemCreator.getQuotaFormulas();
		Collection<CalculateParameter> calculateParameters=getCalculateParametersByQuotaItem(quotaItem.getId());
		if (calculateParameters.size()>0) {
			for (CalculateParameter calculateParameter : calculateParameters) {
				jep.addVariable(calculateParameter.getParameterName(),Double.parseDouble(calculateParameter.getParameterValue()));
			}
		}else {
			System.out.print("quotaItemId��"+quotaItem.getId()+" �޲���������� "+'\n');
		}
		
		Session session=this.getSessionFactory().openSession();
		try {
			if (quotaFormulas.size()>0) {
				if (calculateParameters.size()>0) {
					for (QuotaFormula quotaFormula : quotaFormulas) {
						boolean isCalculateWrong=false;
						jep.parseExpression(quotaFormula.getFormula());
						QuotaFormulaResultValue quotaFormulaResultValue=new QuotaFormulaResultValue();
						quotaFormulaResultValue.setQuotaFormulaResult(quotaFormula.getQuotaFormulaResult());
						quotaFormulaResultValue.setQuotaItem(quotaItem);
						try {
							quotaFormulaResultValue.setValue(new BigDecimal(jep.getValue()).setScale(digit, BigDecimal.ROUND_HALF_UP).doubleValue()+"");
						} catch (Exception e) {
							System.out.print("quotaItemId��"+quotaItem.getId()+" �������� "+'\n');
							isCalculateWrong=true;
						}
						
						if (isCalculateWrong!=true) {
							session.save(quotaFormulaResultValue);
						}else{
							continue;
						}
					}
				}else {
					System.out.print("quotaItemId��"+quotaItem.getId()+" �޲���������� "+'\n');
				}	
			}
		} catch (Exception e) {
			System.out.print(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
	
	//��ȡ����ָ������ļ���������������ֵ�����/�¶�Ŀ��ֵ��
	public Collection<CalculateParameter> getCalculateParametersByQuotaItem(String quotaItemId){
		QuotaItem quotaItem=quotaItemDao.getQuotaItem(quotaItemId);
		ArrayList<CalculateParameter> calculateParameters=new ArrayList<CalculateParameter>();
		
		//��ȡ���ֵ
		String finishValue=quotaItem.getFinishValue();
		String accumulateValue=quotaItem.getAccumulateValue();
		String sameTermValue=quotaItem.getSameTermValue();
		String sameTermAccumulateValue=quotaItem.getSameTermAccumulateValue();
		if (finishValue!=null&&finishValue!="") {
			calculateParameters.add(new CalculateParameter("F",finishValue));//���ֵ
		}
		if (accumulateValue!=null&&accumulateValue!="") {
			calculateParameters.add(new CalculateParameter("A",accumulateValue));//�ۼ�ֵ
		}
		if (sameTermValue!=null&&sameTermValue!="") {
			calculateParameters.add(new CalculateParameter("S",sameTermValue));//ͬ��ֵ
		}
		if (sameTermAccumulateValue!=null&&sameTermAccumulateValue!="") {
			calculateParameters.add(new CalculateParameter("B",sameTermAccumulateValue));//ͬ���ۼ�ֵ
		}
		
		//��ȡ�¶�Ŀ��ֵ
		Collection<QuotaTargetValue> quotaTargetValues=quotaTargetValueDao.getQuotaTargetValuesByQuotaItem(quotaItemId);
		if (quotaTargetValues.size()>0) {
			for (QuotaTargetValue quotaTargetValue : quotaTargetValues) {
				calculateParameters.add(new CalculateParameter(quotaTargetValue.getParameterName(), quotaTargetValue.getValue()+""));
			}
		}
		
		//��ȡ���Ŀ��ֵ
		QuotaItemCreator quotaItemCreator=quotaItem.getQuotaItemCreator();
		Collection<QuotaPropertyValue> quotaPropertyValues=quotaPropertyValueDao.getQuotaPropertyValuesByQuotaItemCreator(quotaItemCreator.getId());
		if (quotaPropertyValues.size()>0) {
			for (QuotaPropertyValue quotaPropertyValue : quotaPropertyValues) {
				calculateParameters.add(new CalculateParameter(quotaPropertyValue.getQuotaProperty().getParameterName(),quotaPropertyValue.getValue()+""));
			}
		}
		return calculateParameters;
	}
}
