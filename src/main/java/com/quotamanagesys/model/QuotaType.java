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

import com.bstek.bdf2.core.model.DefaultDept;

@Component
@Entity
@Table(name = "QUOTA_TYPE")
public class QuotaType implements Serializable{

	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// ����uuid���������ɲ���
	@Column(name = "ID")
	private String id;
	@Column(name="NAME")
	private String quotaTypeName;//ָ����������
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="PROFESSION_ID")
	private QuotaProfession quotaProfession;//ָ��רҵ
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="LEVEL_ID")
	private QuotaLevel quotaLevel;//ָ�꼶��
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="UNIT_ID")
	private QuotaUnit quotaUnit;//ָ�������λ
	@Column(name="DIGIT")
	private int digit;//С��λ��
	@Column(name="RATE")
	private String rate;//����Ƶ��
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="DEPT_ID")
	private DefaultDept manageDept;//������
	@Column(name="IS_USED")
	private boolean inUsed;//ָ������ʹ��״̬
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQuotaTypeName() {
		return quotaTypeName;
	}
	public void setQuotaTypeName(String quotaTypeName) {
		this.quotaTypeName = quotaTypeName;
	}
	public QuotaProfession getQuotaProfession() {
		return quotaProfession;
	}
	public void setQuotaProfession(QuotaProfession quotaProfession) {
		this.quotaProfession = quotaProfession;
	}
	public QuotaLevel getQuotaLevel() {
		return quotaLevel;
	}
	public void setQuotaLevel(QuotaLevel quotaLevel) {
		this.quotaLevel = quotaLevel;
	}
	public QuotaUnit getQuotaUnit() {
		return quotaUnit;
	}
	public void setQuotaUnit(QuotaUnit quotaUnit) {
		this.quotaUnit = quotaUnit;
	}
	public int getDigit() {
		return digit;
	}
	public void setDigit(int digit) {
		this.digit = digit;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public DefaultDept getManageDept() {
		return manageDept;
	}
	public void setManageDept(DefaultDept manageDept) {
		this.manageDept = manageDept;
	}
	public boolean isInUsed() {
		return inUsed;
	}
	public void setInUsed(boolean inUsed) {
		this.inUsed = inUsed;
	}
	
}
