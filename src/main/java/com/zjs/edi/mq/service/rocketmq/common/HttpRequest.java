package com.zjs.edi.mq.service.rocketmq.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zjs.edi.mq.domain.mq.HttpResponse;

/**
* <p>Title: HttpRequest </p>
* <p>@Description: HTTP 交互类  </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月3日 上午11:42:30 
*/
public class HttpRequest
{

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);

	/*
	 * params 填写的URL的参数 encode 字节编码
	 */
	public static HttpResponse sendPostMessage(String url, Map<String, String> params, String encode)
	{

		StringBuffer stringBuffer = new StringBuffer();

		HttpResponse responses = new HttpResponse();

		if (params != null && !params.isEmpty())
		{
			for (Map.Entry<String, String> entry : params.entrySet())
			{
				try
				{
					stringBuffer.append(entry.getKey()).append("=")
							.append(URLEncoder.encode(entry.getValue(), encode)).append("&");

				}
				catch (UnsupportedEncodingException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// 删掉最后一个 & 字符
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);
			LOGGER.debug("将要发送的URL ：" + url + " data " + stringBuffer);
			try
			{
				URL realUrl = new URL(url);
				HttpURLConnection httpURLConnection = (HttpURLConnection) realUrl.openConnection();
				httpURLConnection.setConnectTimeout(3000);
				httpURLConnection.setDoInput(true);// 从服务器获取数据
				httpURLConnection.setDoOutput(true);// 向服务器写入数据

				// 获得上传信息的字节大小及长度
				byte[] mydata = stringBuffer.toString().getBytes();
				// 设置请求体的类型
				httpURLConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				httpURLConnection
						.setRequestProperty("Content-Lenth", String.valueOf(mydata.length));

				// 获得输出流，向服务器输出数据
				OutputStream outputStream = (OutputStream) httpURLConnection.getOutputStream();
				outputStream.write(mydata);

				// 获得服务器响应的结果和状态码
				int responseCode = httpURLConnection.getResponseCode();
				responses.setState(responseCode);
				if (responseCode == 200)
				{
					// 获得输入流，从服务器端获得数据
					InputStream inputStream = (InputStream) httpURLConnection.getInputStream();
					responses.setDate(changeInputStream(inputStream, encode));
				}
				LOGGER.debug(" Web Api 服务响应的  数据   ： " + responses);
			}
			catch (IOException e)
			{
				LOGGER.error(" 调用WEB 服务出现异常  url： " + url + " date:" + stringBuffer, e);
				responses = null;
			}
		}

		return responses;
	}

	/*
	 * // 把从输入流InputStream按指定编码格式encode变成字符串String
	 */
	public static String changeInputStream(InputStream inputStream, String encode)
	{

		// ByteArrayOutputStream 一般叫做内存流
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = "";
		if (inputStream != null)
		{
			try
			{
				while ((len = inputStream.read(data)) != -1)
				{
					byteArrayOutputStream.write(data, 0, len);

				}
				result = new String(byteArrayOutputStream.toByteArray(), encode);

			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

}