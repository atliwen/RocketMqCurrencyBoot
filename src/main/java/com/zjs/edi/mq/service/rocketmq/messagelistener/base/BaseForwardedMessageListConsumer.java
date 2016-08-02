/**
 * 
 */
package com.zjs.edi.mq.service.rocketmq.messagelistener.base;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;

/**
* <p>Title: BaseForwardedMessageListConsumer </p>
* <p>@Description: 转译  转发 消息处理 </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月1日 上午10:04:54 
*/
public interface BaseForwardedMessageListConsumer
{

	/**
	 *  转译  转发 消息处理
	 * @param strBody
	 * @param msg
	 * @param context
	 * @return
	 */
	public String MessageConsumer(String strBody, MessageExt msg, ConsumeConcurrentlyContext context);

}
