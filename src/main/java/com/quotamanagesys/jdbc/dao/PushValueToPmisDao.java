package com.quotamanagesys.jdbc.dao;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.bstek.dorado.data.variant.Record;
import com.bstek.dorado.jdbc.iapi.IJdbcDao;
import com.bstek.dorado.jdbc.iapi.ISelect;

@Component
public class PushValueToPmisDao {
	
	@Resource
    IJdbcDao jdbcDao;
	
	@DataProvider
	public void pagePushValueToPmis(Page<Record> page,Map<String,Object> parameter,Criteria criteria){
	    //通过jdbcDao可得到ISelect、IInsert、IDelete、IUpdate等对象
	    ISelect sel = jdbcDao.newSelect();
	    //查询的Model对象，如果该对象为Table，则不能支持条件查询 ，本例"C_PRODUCTS"为CompositeTable名称
	    sel.model("cpt_push_value_to_pmis");
	    //所需查询的列
	    sel.columns("考核年度","考核月度","指标名称","指标口径","指标归口管理部门","责任部门");
	    //执行查询
	    sel.page(page, parameter, criteria);
	}
	
	@DataProvider
	public void pagePushValueToPmisIsWrong(Page<Record> page,String importStatus){
		ISelect sel = jdbcDao.newSelect();
	    Map<String,Object> map = new HashMap<String, Object>();
	    importStatus="正常";
	    //该参数的key值必须与之前我们配置过的查询参数value属性、即与 :CATEGORY_ID一致。
	    map.put("导入情况监控", importStatus);
	    sel.model("cpt_push_value_to_pmis");
	    sel.page(page, map);
	}
}
