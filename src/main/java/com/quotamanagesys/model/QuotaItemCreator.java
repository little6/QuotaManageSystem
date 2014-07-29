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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
	private String name;//ָ������
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
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="DIMENSION_ONE_ID")
	private QuotaDimensionOne quotaDimensionOne;//һά
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="DIMENSION_TWO_ID")
	private QuotaDimensionTwo quotaDimensionTwo;//��ά
	
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
	public QuotaDimensionOne getQuotaDimensionOne() {
		return quotaDimensionOne;
	}
	public void setQuotaDimensionOne(QuotaDimensionOne quotaDimensionOne) {
		this.quotaDimensionOne = quotaDimensionOne;
	}
	public QuotaDimensionTwo getQuotaDimensionTwo() {
		return quotaDimensionTwo;
	}
	public void setQuotaDimensionTwo(QuotaDimensionTwo quotaDimensionTwo) {
		this.quotaDimensionTwo = quotaDimensionTwo;
	}
}
