package com.zjs.edi.mq.service.rocketmq.common;

import java.util.List;

import com.alibaba.rocketmq.common.message.MessageExt;

/**
* <p>Title: MessageHelp </p>
* <p>@Description: 消息集合帮助类  </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月1日 上午10:06:08 
*/
public class MessageHelp
{

	/**
	 * 将当前的消息集合 转换成字符串 方便记录日志 PS StringBuffer不是一个线程安全的类 将其放在一个方法中 局部变量希望能避免出现问题
	 * 
	 * @param msgs
	 * @return
	 */
	public static String GetMessagesToString(List<MessageExt> msgs)
	{
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(" 默认只处理 下标为 0 的 消息    当前消息数量是 ： ").append(msgs.size());
		for (MessageExt messageExt : msgs)
			sBuffer.append(" 消息  ：").append(messageExt.toString()).append("   ");
		return sBuffer.toString();
	}

}
