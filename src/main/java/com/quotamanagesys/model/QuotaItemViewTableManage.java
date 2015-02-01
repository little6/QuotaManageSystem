package com.quotamanagesys.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name = "QUOTA_ITEM_VIEW_TABLE_MANAGE")
public class QuotaItemViewTableManage {

	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// ����uuid���������ɲ���
	@Column(name = "ID")
	String id;
	@Column(name="TABLE_NAME")
	String tableName;//���ɹ���Ϊ:quota_item_view_xxxx,xxxxΪ4λ�������
	@Column(name="SHOW_NAME")
	String showName;//���ɹ���Ϊ:xxxx��ָ����Ϣ�ܱ�,xxxxΪ4λ�������
	@Column(name="YEAR")
	int year;//ָ����Ϣ�ܱ��������
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getShowName() {
		return showName;
	}
	public void setShowName(String showName) {
		this.showName = showName;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	
}
