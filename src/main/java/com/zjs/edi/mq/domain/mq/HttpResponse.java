package com.zjs.edi.mq.domain.mq;

public class HttpResponse
{

	/**
	 * HTTP 响应状态
	 */
	private int state;

	/**
	 * 响应是内容
	 */
	private String date;

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
	public String getDate()
	{
		return date;
	}

	/**  
	 * 设置响应是内容  
	 * @param date 响应是内容  
	 */
	public void setDate(String date)
	{
		this.date = date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "HttpResponse [state=" + state + ", date=" + date + "]";
	}

}
