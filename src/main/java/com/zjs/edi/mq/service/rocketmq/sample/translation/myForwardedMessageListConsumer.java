/**
 * 
 */
package com.zjs.edi.mq.service.rocketmq.sample.translation;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.zjs.edi.mq.service.rocketmq.messagelistener.Interface.ForwardedMessageListConsumerInterface;

/**
* <p>Title: myForwardedMessageListConsumer </p>
* <p>@Description: 示例 转发模式 消息加工处理类  </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年5月31日 下午3:56:07 
*/
public class myForwardedMessageListConsumer implements ForwardedMessageListConsumerInterface
{

	@Override
	public String MessageConsumer(String strBody, MessageExt msg, ConsumeConcurrentlyContext context)
	{

		return strBody + "我了个去";
	}

}
