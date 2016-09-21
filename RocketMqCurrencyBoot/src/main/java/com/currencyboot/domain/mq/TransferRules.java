package com.currencyboot.domain.mq;

public class TransferRules
{

	/**
	 *  消费的 Tag 
	 */
	private String tag;

	/**
	 *  Body 中包含的 字符串
	 */
	private String body;

	/**
	 *  调用的 webapi 地址
	 */
	private String url;

	/**
	 * 转发的 Topic 不设置 就不进行 转发消息 
	 */
	private String topic;

	/**
	 * 转发的 Tag 不设置 就不进行 转发消息
	 */
	private String tags;

	/**  
	 * 获取消费的Tag  
	 * @return tag 消费的Tag  
	 */
	public String getTag()
	{
		return tag;
	}

	/**  
	 * 设置消费的Tag  
	 * @param tag 消费的Tag  
	 */
	public void setTag(String tag)
	{
		this.tag = tag;
	}

	/**  
	 * 获取Body中包含的字符串  
	 * @return body Body中包含的字符串  
	 */
	public String getBody()
	{
		return body;
	}

	/**  
	 * 设置Body中包含的字符串  
	 * @param body Body中包含的字符串  
	 */
	public void setBody(String body)
	{
		this.body = body;
	}

	/**  
	 * 获取调用的webapi地址  
	 * @return url 调用的webapi地址  
	 */
	public String getUrl()
	{
		return url;
	}

	/**  
	 * 设置调用的webapi地址  
	 * @param url 调用的webapi地址  
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**  
	 * 获取转发的Topic不设置就不进行转发消息  
	 * @return topic 转发的Topic不设置就不进行转发消息  
	 */
	public String getTopic()
	{
		return topic;
	}

	/**  
	 * 设置转发的Topic不设置就不进行转发消息  
	 * @param topic 转发的Topic不设置就不进行转发消息  
	 */
	public void setTopic(String topic)
	{
		this.topic = topic;
	}

	/**  
	 * 获取转发的Tag不设置就不进行转发消息  
	 * @return tags 转发的Tag不设置就不进行转发消息  
	 */
	public String getTags()
	{
		return tags;
	}

	/**  
	 * 设置转发的Tag不设置就不进行转发消息  
	 * @param tags 转发的Tag不设置就不进行转发消息  
	 */
	public void setTags(String tags)
	{
		this.tags = tags;
	}

}
