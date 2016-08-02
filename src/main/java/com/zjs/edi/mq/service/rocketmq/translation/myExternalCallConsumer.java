/**
 * 
 */
package com.zjs.edi.mq.service.rocketmq.translation;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.zjs.edi.mq.service.rocketmq.messagelistener.base.BaseExternalCallConsumer;

/**
* <p>Title: myExternalCallConsumer </p>
* <p>@Description: TODO </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年5月31日 下午3:41:47 
*/
public class myExternalCallConsumer implements BaseExternalCallConsumer
{

	@Override
	public String MessageConsumer(String strBody, MessageExt msg, ConsumeConcurrentlyContext context)
	{

		return "我了个去";
	}

}
