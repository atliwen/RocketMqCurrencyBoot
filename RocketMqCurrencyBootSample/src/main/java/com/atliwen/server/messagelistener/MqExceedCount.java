package com.atliwen.server.messagelistener;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.currencyboot.service.rocketmq.messagelistener.Interface.MqExceedCountInterface;

/**
* <p>Title: MqExceedCount </p>
* <p>@Description: 消费超出 设置的容错 次数 后触发的 方法类 </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年9月21日 下午2:40:37 
*/
public class MqExceedCount implements MqExceedCountInterface
{
	@Override
	public void exceedCount(String strBody, MessageExt msg, ConsumeConcurrentlyContext context)
	{

	}
}
