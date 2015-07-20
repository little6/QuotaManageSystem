package com.quotamanagesys.interceptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.uploader.DownloadFile;
import com.bstek.dorado.uploader.annotation.FileProvider;

@Component
public class UserHelpCenter extends HibernateDao {

	@FileProvider
	public DownloadFile getHelpBook(Map<String, String> parameter)
			throws IOException {
		String helpName = parameter.get("helpname");
		String fileName = null;
		
		switch (helpName) {
		case "QuotaItemCreatorMaintainForUser":
			fileName = "C:\\DC_\\ָ���ʼ��˵��.doc";
			break;
		case "QuotaItemMonthFinishValueMaintainForUser":
			fileName = "C:\\DC_\\�¶����ֵ��д˵��.doc";
			break;
		case "QuotaItemTargetValueMaintainForUser":
			fileName = "C:\\DC_\\�¶�Ŀ��ֵ�༭˵��.doc";
			break;
		case "QuotaItemView":
			fileName = "C:\\DC_\\ָ�����˵��.doc";
			break;
		case "QuotaItemYearFinishValueMaintainForUser":
			fileName = "C:\\DC_\\������ֵ��д˵��.doc";
			break;
		case "QuotaTypeMaintain":
			fileName = "C:\\DC_\\ָ������༭˵��.doc";
			break;
		case "QuotaTypeFormulaLinkMaintain":
			fileName = "C:\\DC_\\�������㹫ʽ˵��.doc";
			break;
		default:
			break;
		}

		InputStream inputStream = new FileInputStream(fileName);
		DownloadFile downloadFile = new DownloadFile(URLEncoder.encode(
				fileName, "UTF-8"), inputStream);
		return downloadFile;
	}

}
