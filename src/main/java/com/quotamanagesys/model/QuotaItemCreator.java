package com.quotamanagesys.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.model.DefaultDept;

@Component
@Entity
@Table(name = "QUOTA_ITEM_CREATOR")
public class QuotaItemCreator implements Serializable{

	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// ����uuid���������ɲ���
	@Column(name = "ID")
	private String id;
	@Column(name="NAME")
	private String name;//ָ������������(����ָ����������&&����ָ������)
	@Column(name="YEAR")
	private int year;//ָ�����
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="QUOTA_TYPE_ID")
	private QuotaType quotaType;//ָ������
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="QUOTA_COVER_ID")
	private QuotaCover quotaCover;//�ھ�
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="DUTY_DEPT_ID")
	private DefaultDept quotaDutyDept;//ָ�����β���
	@ManyToMany(fetch = FetchType.EAGER, targetEntity = QuotaFormula.class, cascade = CascadeType.ALL)
	@JoinTable(name = "FORMULA_QUOTA_ITEM_CREATOR_MAP", joinColumns = { @JoinColumn(name = "QUOTA_ITEM_CREATOR_ID") }, inverseJoinColumns = { @JoinColumn(name = "QUOTA_FORMULA_ID") })
	private Set<QuotaFormula> quotaFormulas;//�����ļ��㹫ʽ
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
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public QuotaType getQuotaType() {
		return quotaType;
	}
	public void setQuotaType(QuotaType quotaType) {
		this.quotaType = quotaType;
	}
	public QuotaCover getQuotaCover() {
		return quotaCover;
	}
	public void setQuotaCover(QuotaCover quotaCover) {
		this.quotaCover = quotaCover;
	}
	public DefaultDept getQuotaDutyDept() {
		return quotaDutyDept;
	}
	public void setQuotaDutyDept(DefaultDept quotaDutyDept) {
		this.quotaDutyDept = quotaDutyDept;
	}
	public Set<QuotaFormula> getQuotaFormulas() {
		return quotaFormulas;
	}
	public void setQuotaFormulas(Set<QuotaFormula> quotaFormulas) {
		this.quotaFormulas = quotaFormulas;
	}
	
}
