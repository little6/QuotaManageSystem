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
@Table(name = "SHOW_COLUMN_GROUP")
public class ShowColumnGroup implements Serializable{
	
	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// ����uuid���������ɲ���
	@Column(name = "ID")
	private String id;
	@Column(name="NAME")
	private String name;//�з�������
	@Column(name="SORT")
	private int sort;//����
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="QUOTA_ITEM_VIEW_TABLE_MANAGE_ID")
	private QuotaItemViewTableManage quotaItemViewTableManage;//����ָ����Ϣ�ܱ�
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="FATHER_SHOW_COLUMN_GROUP_ID")
	private ShowColumnGroup fatherShowColumnGroup;//�ϼ�����
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
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public QuotaItemViewTableManage getQuotaItemViewTableManage() {
		return quotaItemViewTableManage;
	}
	public void setQuotaItemViewTableManage(
			QuotaItemViewTableManage quotaItemViewTableManage) {
		this.quotaItemViewTableManage = quotaItemViewTableManage;
	}
	public ShowColumnGroup getFatherShowColumnGroup() {
		return fatherShowColumnGroup;
	}
	public void setFatherShowColumnGroup(ShowColumnGroup fatherShowColumnGroup) {
		this.fatherShowColumnGroup = fatherShowColumnGroup;
	}
	
}
