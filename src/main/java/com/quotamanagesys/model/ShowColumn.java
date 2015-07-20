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
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// 采用uuid的主键生成策略
	@Column(name = "ID")
	private String id;
	@Column(name="NAME")
	private String name;//列名
	@Column(name="ALIAS")
	private String alias;//别名
	@Column(name="WIDTH")
	private int width;//列宽
	@Column(name="IS_WRAPPABLE")
	private boolean wrappable;//是否可换行
	@Column(name="IS_VISIBLE")
	private boolean visible;//是否显示
	@Column(name="ALIGN")
	private String align;//对齐方式
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="SHOW_COLUMN_GROUP_ID")
	private ShowColumnGroup showColumnGroup;//所属列分组
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="QUOTA_ITEM_VIEW_TABLE_MANAGE_ID")
	private QuotaItemViewTableManage quotaItemViewTableManage;//所属指标信息总表
	@Column(name="SORT")
	private int sort;//排序
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="RENDER_ID")
	private Render render;//渲染器
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="SHOW_COLUMN_TRIGGER_ID")
	private ShowColumnTrigger showColumnTrigger;
	@Column(name="FILTER_MAP_COLUMN_NAME")
	private String filterMapColumnName;
	
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
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
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
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public ShowColumnTrigger getShowColumnTrigger() {
		return showColumnTrigger;
	}
	public void setShowColumnTrigger(ShowColumnTrigger showColumnTrigger) {
		this.showColumnTrigger = showColumnTrigger;
	}
	public QuotaItemViewTableManage getQuotaItemViewTableManage() {
		return quotaItemViewTableManage;
	}
	public void setQuotaItemViewTableManage(
			QuotaItemViewTableManage quotaItemViewTableManage) {
		this.quotaItemViewTableManage = quotaItemViewTableManage;
	}
	public String getFilterMapColumnName() {
		return filterMapColumnName;
	}
	public void setFilterMapColumnName(String filterMapColumnName) {
		this.filterMapColumnName = filterMapColumnName;
	}
	
}
