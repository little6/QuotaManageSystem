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
        // ���ر����ļ�
        String fileName = "Operator.doc".toString(); // �ļ���Ĭ�ϱ�����
        // ��������
        InputStream inStream = new FileInputStream("c:/Operator.doc");// �ļ��Ĵ��·��
        // ��������ĸ�ʽ
        response.reset();
        response.setContentType("bin");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        // ѭ��ȡ�����е�����
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
		String fileName="C:\\DC_\\"+URLEncoder.encode("�ӳع����"+year+"��"+month+"�¹ؼ�ҵ������ָ����������", "UTF-8")+".xls";
	    DownloadFile downloadFile= new DownloadFile(new File(fileName));
	    return downloadFile;
	}
	
	@FileProvider
	public DownloadFile download2(Map<String,String> parameter) throws IOException{
		String year=parameter.get("year");
		String month=parameter.get("month");
		String fileName="C:\\DC_\\"+URLEncoder.encode("�ӳع����"+year+"��"+month+"��ָ�����������칫�ҹ�עָ�꣩", "UTF-8")+".xls";
		DownloadFile downloadFile= new DownloadFile(new File(fileName));
	    return downloadFile;
	}
	
}
