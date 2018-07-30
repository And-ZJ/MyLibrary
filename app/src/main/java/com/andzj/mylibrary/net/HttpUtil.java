package com.andzj.mylibrary.net;



import android.os.Handler;
import android.os.Message;

import com.andzj.mylibrary.activity.LoginActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by zj on 2016/11/15.
 */

public class HttpUtil {

    public static void sendHttpRequest(final String address,final String uploadStr,final HttpCallbackListener listener)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try
                {
                    URL url = new URL(address);

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    if (uploadStr != null)
                    {
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                        outputStream.writeBytes(uploadStr);
                    }

                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        response.append(line);
                    }
                    if (listener != null)
                    {
                        listener.onFinish(response.toString());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    if (listener != null)
                    {
                        listener.onError(e);
                    }
                }
                finally {
                    if (connection != null)
                    {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private static String BOUNDARY = java.util.UUID.randomUUID().toString();
    private static String PREFIX = "--", LINEND = "\r\n";
    private static String MULTIPART_FROM_DATA = "multipart/form-data";
    private static String CHARSET = "UTF-8";

    public static void sendHttpImageFile(final String address,final Map<String,String> uploadMap,final File imageFile,final HttpCallbackListener listener)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

                    DataOutputStream outputStream =  new DataOutputStream(connection.getOutputStream());

                    StringBuffer sb = new StringBuffer(); //用StringBuilder拼接报文，用于上传图片数据
                    sb.append(PREFIX);
                    sb.append(BOUNDARY);
                    sb.append(LINEND);
                    sb.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(imageFile.getName()).append("\"").append(LINEND);
                    sb.append("Content-Type: image/jpg; charset=").append(CHARSET).append(LINEND);
                    sb.append(LINEND);
                    outputStream.write(sb.toString().getBytes());

                    InputStream fileInputStream = new FileInputStream(imageFile);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len); //写入图片数据
                    }
                    fileInputStream.close();
                    outputStream.write(LINEND.getBytes());

                    if (uploadMap !=null)
                    {
                        StringBuilder text = new StringBuilder();
                        for(Map.Entry<String,String> entry : uploadMap.entrySet()) { //在for循环中拼接报文，上传文本数据
                            text.append("--");
                            text.append(BOUNDARY);
                            text.append("\r\n");
                            text.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
                            text.append(entry.getValue());
                            //text.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
                            text.append("\r\n");
                        }
                        outputStream.write(text.toString().getBytes("UTF-8")); //写入文本数据
                    }

                    // 请求结束标志
                    byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
                    outputStream.write(end_data);

                    outputStream.flush();
                    outputStream.close();



                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        response.append(line);
                    }
                    if (listener != null)
                    {
                        listener.onFinish(response.toString());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    if (listener != null)
                    {
                        listener.onError(e);
                    }
                }
                finally
                {
                    if (connection != null)
                    {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
