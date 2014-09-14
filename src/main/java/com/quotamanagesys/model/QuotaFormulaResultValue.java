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
@Table(name = "QUOTA_FORMULA_RESULT_VALUE")
public class QuotaFormulaResultValue implements Serializable{

	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// ����uuid���������ɲ���
	@Column(name = "ID")
	private String id;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="QUOTA_FORMULA_RESULT_ID")
	private QuotaFormulaResult quotaFormulaResult;//��ʽ�������
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="QUOTA_ITEM_ID")
	private QuotaItem quotaItem;//�����ù�ʽ�����ָ��
	@Column(name="VALUE")
	private String value;//��ʽ���ֵ
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
	public QuotaItem getQuotaItem() {
		return quotaItem;
	}
	public void setQuotaItem(QuotaItem quotaItem) {
		this.quotaItem = quotaItem;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
