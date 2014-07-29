package com.quotamanagesys.model;

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
@Table(name = "QUOTA_PROPERTY_VALUE")
public class QuotaPropertyValue {

	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// ����uuid���������ɲ���
	@Column(name = "ID")
	private String id;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="PROPERTY_ID")
	private QuotaProperty quotaProperty;//ָ����������
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="QUOTA_ITEM_CREATOR_ID")
	private QuotaItemCreator quotaItemCreator;//ָ��������
	@Column(name="VALUE")
	private double value;//ָ������Ŀ��ֵ
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public QuotaProperty getQuotaProperty() {
		return quotaProperty;
	}
	public void setQuotaProperty(QuotaProperty quotaProperty) {
		this.quotaProperty = quotaProperty;
	}
	public QuotaItemCreator getQuotaItemCreator() {
		return quotaItemCreator;
	}
	public void setQuotaItemCreator(QuotaItemCreator quotaItemCreator) {
		this.quotaItemCreator = quotaItemCreator;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
}
