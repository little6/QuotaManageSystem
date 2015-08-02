package com.quotamanagesys.tools;

import java.util.Calendar;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.model.UrlComponent;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.view.widget.HtmlContainer;
import com.bstek.dorado.web.DoradoContext;
import com.quotamanagesys.interceptor.WriteExcel;

@Component
public class KeyPointQuotaItemViewInit extends HibernateDao{
	
	@Resource
	WriteExcel writeExcel;
	@Resource
	PoiReadExcel poiReadExcel;
	
	public void Hc1Init(HtmlContainer hc1) throws Exception{
		int year;
		int month;
		DoradoContext context = DoradoContext.getCurrent();
		
		if (context.getAttribute(DoradoContext.VIEW, "year")==null) {
			Calendar calendar=Calendar.getInstance();	
			year=calendar.get(Calendar.YEAR);
		} else {
			year=(int) context.getAttribute(DoradoContext.VIEW, "year");
		}
		if (context.getAttribute(DoradoContext.VIEW, "month")==null) {
			Calendar calendar=Calendar.getInstance();	
			//Ĭ����ʾ��������
			month=calendar.get(Calendar.MONTH);//calendar����ʵ�·���Ҫ+1,��Ϊcalendar���·ݴ�0��ʼ
			if (month==0) {
				year=year-1;
				month=12;
			}
		} else {
			month=(int) context.getAttribute(DoradoContext.VIEW, "month");
		}
		
		writeExcel.writeExcel(year, month);
		String htmlString=poiReadExcel.getHtml();
		
		hc1.setContent(htmlString);
	}
	
}
