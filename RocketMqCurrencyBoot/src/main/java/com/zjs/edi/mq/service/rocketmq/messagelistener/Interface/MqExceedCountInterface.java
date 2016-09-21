package com.zjs.edi.mq.service.rocketmq.messagelistener.Interface;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;

/**
* <p>Title: MqExceedCountInterface </p>
* <p>@Description: Mq超出 设置的处理次数 触发的 接口类 </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月24日 下午2:53:52 
*/
public interface MqExceedCountInterface
{
	void exceedCount(String strBody, MessageExt msg, ConsumeConcurrentlyContext context);
}
