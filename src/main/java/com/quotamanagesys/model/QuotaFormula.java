package com.quotamanagesys.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name = "QUOTA_FORMULA")
public class QuotaFormula implements Serializable{

	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// ����uuid���������ɲ���
	@Column(name = "ID")
	private String id;
	@Column(name="NAME")
	private String name;//��ʽ����
	@Column(name="FORMULA")
	private String formula;//��ʽ
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="QUOTA_FORMULA_RESULT_ID")
	private QuotaFormulaResult quotaFormulaResult;//��ʽ����������
	@Column(name="REMARK")
	private String remark;//��ע
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public QuotaFormulaResult getQuotaFormulaResult() {
		return quotaFormulaResult;
	}
	public void setQuotaFormulaResult(QuotaFormulaResult quotaFormulaResult) {
		this.quotaFormulaResult = quotaFormulaResult;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
