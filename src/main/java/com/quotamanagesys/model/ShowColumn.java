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
@Table(name = "SHOW_COLUMN")
public class ShowColumn implements Serializable{

	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// ����uuid���������ɲ���
	@Column(name = "ID")
	private String id;
	@Column(name="NAME")
	private String name;//����
	@Column(name="WIDTH")
	private int width;//�п�
	@Column(name="IS_WRAPPABLE")
	private boolean wrappable;//�Ƿ�ɻ���
	@Column(name="IS_VISIBLE")
	private boolean visible;//�Ƿ���ʾ
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="SHOW_COLUMN_GROUP_ID")
	private ShowColumnGroup showColumnGroup;//�����з���
	@Column(name="SORT")
	private int sort;//����
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="RENDER_ID")
	private Render render;//��Ⱦ��
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
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public boolean getWrappable() {
		return wrappable;
	}
	public void setWrappable(boolean wrappable) {
		this.wrappable = wrappable;
	}
	public boolean getVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public ShowColumnGroup getShowColumnGroup() {
		return showColumnGroup;
	}
	public void setShowColumnGroup(ShowColumnGroup showColumnGroup) {
		this.showColumnGroup = showColumnGroup;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public Render getRender() {
		return render;
	}
	public void setRender(Render render) {
		this.render = render;
	}
	
}
