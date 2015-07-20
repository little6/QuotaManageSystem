package com.quotamanagesys.interceptor;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.uploader.DownloadFile;
import com.bstek.dorado.uploader.annotation.FileProvider;

@Component
public class DownLoadFile {

	/*
	@Expose
	public void downloadLocal(HttpServletResponse response) throws FileNotFoundException {
        // 下载本地文件
        String fileName = "Operator.doc".toString(); // 文件的默认保存名
        // 读到流中
        InputStream inStream = new FileInputStream("c:/Operator.doc");// 文件的存放路径
        // 设置输出的格式
        response.reset();
        response.setContentType("bin");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        // 循环取出流中的数据
        byte[] b = new byte[100];
        int len;
        try {
            while ((len = inStream.read(b)) > 0)
                response.getOutputStream().write(b, 0, len);
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
    */
	
	@FileProvider
	public DownloadFile download(Map<String,String> parameter) throws IOException{
		String year=parameter.get("year");
		String month=parameter.get("month");
		String fileName="C:\\DC_\\河池供电局X年X月关键业绩考核指标完成情况表.xls";
		//String fileName="C:\\DC_\\河池供电局"+year+"年"+month+"月关键业绩考核指标完成情况表.xlsx";
		
	    DownloadFile downloadFile= new DownloadFile(new File(URLEncoder.encode(fileName, "UTF-8")));
	    return downloadFile;
	}
	
}
