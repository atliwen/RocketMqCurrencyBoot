package com.zjs.edi.mq.service.rocketmq.messagelistener.Interface;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;

/**
* <p>Title: BaseMessageListenerConsumer </p>
* <p>@Description: 自定义消费 接口     继承该接口   实现消费方法   </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月1日 上午10:05:04 
*/
public interface MessageListenerConsumerInterface
{

	/**
	 *   自定义消费方法  
	 * 
	 * @param msg
	 * @param context
	 * @return
	 */
	public ConsumeConcurrentlyStatus consumeMessage(String strBody, MessageExt msg,
			ConsumeConcurrentlyContext context);
}
