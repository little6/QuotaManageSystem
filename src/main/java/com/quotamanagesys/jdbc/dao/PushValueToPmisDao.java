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
	    //ͨ��jdbcDao�ɵõ�ISelect��IInsert��IDelete��IUpdate�ȶ���
	    ISelect sel = jdbcDao.newSelect();
	    //��ѯ��Model��������ö���ΪTable������֧��������ѯ ������"C_PRODUCTS"ΪCompositeTable����
	    sel.model("cpt_push_value_to_pmis");
	    //�����ѯ����
	    sel.columns("�������","�����¶�","ָ������","ָ��ھ�","ָ���ڹ�����","���β���");
	    //ִ�в�ѯ
	    sel.page(page, parameter, criteria);
	}
	
	@DataProvider
	public void pagePushValueToPmisIsWrong(Page<Record> page,String importStatus){
		ISelect sel = jdbcDao.newSelect();
	    Map<String,Object> map = new HashMap<String, Object>();
	    importStatus="����";
	    //�ò�����keyֵ������֮ǰ�������ù��Ĳ�ѯ����value���ԡ����� :CATEGORY_IDһ�¡�
	    map.put("����������", importStatus);
	    sel.model("cpt_push_value_to_pmis");
	    sel.page(page, map);
	}
}
