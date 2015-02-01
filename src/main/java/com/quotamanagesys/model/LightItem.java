package com.quotamanagesys.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name = "LIGHT_ITEM")
public class LightItem {

	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// ����uuid���������ɲ���
	@Column(name = "ID")
	private String id;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "QUOTA_FORMULA_RESULT_ID", unique = true)
	private QuotaFormulaResult quotaFormulaResult;//����������Ĺ�ʽ�����һ��һ��ϵ
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public QuotaFormulaResult getQuotaFormulaResult() {
		return quotaFormulaResult;
	}
	public void setQuotaFormulaResult(QuotaFormulaResult quotaFormulaResult) {
		this.quotaFormulaResult = quotaFormulaResult;
	}
	
}