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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

public class QuotaItemMonth implements Serializable{
	
	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// ����uuid���������ɲ���
	@Column(name = "ID")
	private String id;
	@Column(name="ITEM_NAME")
	private String quotaItemName;//ָ������
	@Column(name="YEAR")
	private int year;//���
	@Column(name="MONTH")
	private int month;//�¶�
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="QUOTA_COVER_ID")
	private QuotaCover quotaCover;//�ھ�
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="QUOTA_TYPE_ID")
	private QuotaType quotaType;//ָ������
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="DIMENSION_ONE_ID")
	private QuotaDimensionOne quotaDimensionOne;//һά
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="DIMENSION_TWO_ID")
	private QuotaDimensionTwo quotaDimensionTwo;//��ά
	@ManyToMany(fetch = FetchType.EAGER, targetEntity = QuotaProperty.class, cascade = CascadeType.ALL)
	@JoinTable(name = "PROPERTY_QUOTA_ITEM_MONTH_MAP", joinColumns = { @JoinColumn(name = "QUOTA_ITEM_MONTH_ID") }, inverseJoinColumns = { @JoinColumn(name = "QUOTA_PROPERTY_ID") })
	private Set<QuotaProperty> quotaProperties;//ָ������
	@OneToMany(fetch = FetchType.EAGER, targetEntity = QuotaPropertyValue.class, cascade = CascadeType.ALL)
	@JoinColumns(value = { @JoinColumn(name = "QUOTA_ITEM_ID", referencedColumnName = "ID")})
	private Set<QuotaPropertyValue> quotaPropertyValues;//ָ������Ŀ��ֵ
	@Column(name="TARGET_VALUE")
	private double targetValue;//����Ŀ��ֵ
	@Column(name="VALUE")
	private double value;//���ֵ
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQuotaItemName() {
		return quotaItemName;
	}
	public void setQuotaItemName(String quotaItemName) {
		this.quotaItemName = quotaItemName;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public QuotaCover getQuotaCover() {
		return quotaCover;
	}
	public void setQuotaCover(QuotaCover quotaCover) {
		this.quotaCover = quotaCover;
	}
	public QuotaType getQuotaType() {
		return quotaType;
	}
	public void setQuotaType(QuotaType quotaType) {
		this.quotaType = quotaType;
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
	public Set<QuotaProperty> getQuotaProperties() {
		return quotaProperties;
	}
	public void setQuotaProperties(Set<QuotaProperty> quotaProperties) {
		this.quotaProperties = quotaProperties;
	}
	public Set<QuotaPropertyValue> getQuotaPropertyValues() {
		return quotaPropertyValues;
	}
	public void setQuotaPropertyValues(Set<QuotaPropertyValue> quotaPropertyValues) {
		this.quotaPropertyValues = quotaPropertyValues;
	}
	public double getTargetValue() {
		return targetValue;
	}
	public void setTargetValue(double targetValue) {
		this.targetValue = targetValue;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
}
