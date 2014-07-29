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
@Table(name = "QUOTA_DIMENSION_TWO")
public class QuotaDimensionTwo implements Serializable{

	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// ����uuid���������ɲ���
	@Column(name = "ID")
	private String id;
	@Column(name="NAME")
	private String dimensionName;//��ά����
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="DIMENSION_ONE_ID")
	private QuotaDimensionOne quotaDimensionOne;//����һά
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDimensionName() {
		return dimensionName;
	}
	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}
	public QuotaDimensionOne getQuotaDimensionOne() {
		return quotaDimensionOne;
	}
	public void setQuotaDimensionOne(QuotaDimensionOne quotaDimensionOne) {
		this.quotaDimensionOne = quotaDimensionOne;
	}
	
}
