package com.tmall.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadService {
    /**
     * 以POST方式上传文件
     *
     * @param requestUrl
     * @param file
     * @return
     */
    public static String postUseUrlConnection(String requestUrl, File file) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        StringBuffer sb = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // 允许Input、Output，不使用Cache
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            // 设置以POST方式进行传送
            con.setRequestMethod("POST");
            // 设置RequestProperty
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            // 构造DataOutputStream流
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; "
                    + "name=\"file1\";filename=\"" + file.getName() + "\"" + end);
            ds.writeBytes(end);
            // 构造要上传文件的FileInputStream流
            FileInputStream fis = new FileInputStream(file);
            // 设置每次写入1024bytes
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            // 从文件读取数据至缓冲区
            while ((length = fis.read(buffer)) != -1) {
                // 将资料写入DataOutputStream中
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            // 关闭流
            fis.close();
            ds.flush();
            // 获取响应流
            InputStream is = con.getInputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                sb.append((char) ch);
            }
            // 关闭DataOutputStream
            ds.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
