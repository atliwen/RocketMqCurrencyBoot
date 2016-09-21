package com.zjs.edi.mq.domain.mq;

/**
* <p>Title: HttpResponse </p>
* <p>@Description: HTTP请求响应数据类 </p>
* <p>Company:  </p>
* @author 李文 
* @date   2016年8月3日 上午11:40:56 
*/
public class HttpResponse
{

	/**
	 * HTTP 响应状态
	 */
	private int state;

	/**
	 * 响应是内容
	 */
	private String data;

	/**  
	 * 获取HTTP响应状态  
	 * @return state HTTP响应状态  
	 */
	public int getState()
	{
		return state;
	}

	/**  
	 * 设置HTTP响应状态  
	 * @param state HTTP响应状态  
	 */
	public void setState(int state)
	{
		this.state = state;
	}

	/**  
	 * 获取响应是内容  
	 * @return date 响应是内容  
	 */
	public String getData()
	{
		return data;
	}

	/**  
	 * 设置响应是内容  
	 * @param date 响应是内容  
	 */
	public void setDate(String data)
	{
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "HttpResponse [state=" + state + ", date=" + data + "]";
	}

	public HttpResponse()
	{
	}

	public HttpResponse(int state, String data)
	{
		super();
		this.state = state;
		this.data = data;
	}

}
