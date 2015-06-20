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

import com.bstek.bdf2.core.model.DefaultUser;

@Component
@Entity
@Table(name = "QUOTA_ITEM_VIEW_MAP")
public class QuotaItemViewMap {

	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// 采用uuid的主键生成策略
	private String id;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="USER_ID")
	private DefaultUser user;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="QUOTA_TYPE_ID")
	private QuotaType quotaType;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="QUOTA_COVER_ID")
	private QuotaCover quotaCover;
	@Column(name="CAN_VIEW")
	private boolean canView;
	@Column(name="DEFAULT_VIEW")
	private boolean defaultView;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public DefaultUser getUser() {
		return user;
	}
	public void setUser(DefaultUser user) {
		this.user = user;
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
	public boolean isCanView() {
		return canView;
	}
	public void setCanView(boolean canView) {
		this.canView = canView;
	}
	public boolean isDefaultView() {
		return defaultView;
	}
	public void setDefaultView(boolean defaultView) {
		this.defaultView = defaultView;
	}
	
}
