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
			fileName = "C:\\DC_\\指标初始化说明.doc";
			break;
		case "QuotaItemMonthFinishValueMaintainForUser":
			fileName = "C:\\DC_\\月度完成值填写说明.doc";
			break;
		case "QuotaItemTargetValueMaintainForUser":
			fileName = "C:\\DC_\\月度目标值编辑说明.doc";
			break;
		case "QuotaItemView":
			fileName = "C:\\DC_\\指标浏览说明.doc";
			break;
		case "QuotaItemYearFinishValueMaintainForUser":
			fileName = "C:\\DC_\\年度完成值填写说明.doc";
			break;
		case "QuotaTypeMaintain":
			fileName = "C:\\DC_\\指标种类编辑说明.doc";
			break;
		case "QuotaTypeFormulaLinkMaintain":
			fileName = "C:\\DC_\\关联计算公式说明.doc";
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
