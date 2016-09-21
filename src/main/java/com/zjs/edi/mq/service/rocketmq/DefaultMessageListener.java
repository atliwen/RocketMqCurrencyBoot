package com.zjs.edi.mq.service.rocketmq;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.zjs.edi.mq.service.rocketmq.common.MessageHelp;
import com.zjs.edi.mq.service.rocketmq.messagelistener.Interface.MessageListenerConsumerInterface;
import com.zjs.edi.mq.service.rocketmq.messagelistener.Interface.MqExceedCountInterface;

/**
* <p>Title: DefaultMessageListener </p>
* <p>@Description: 默认消费方法类  MessageListenerConcurrently 模式
*  
*    使用Spring 来完成注入    
*    
* </p>
* <p>Company:  </p> 
* @author 李文
* @date   2016年8月1日 上午10:06:08 
*/
@Component
public class DefaultMessageListener implements MessageListenerConcurrently
{

	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultMessageListener.class);

	/**
	 *   消息处理次数   
	 */
	@Value("${MQ.COUNT:12}")
	private int count;

	/**
	 *  数据字符集
	 */
	@Value("${MQ.Encoding:UTF-8}")
	private String Encoding;

	/**
	 *  自定义的消费方法
	 */
	// @Resource(name = "consumer")
	@Autowired
	private MessageListenerConsumerInterface consumer;

	@Autowired(required = false)
	private MqExceedCountInterface mqExceedCount;

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
			ConsumeConcurrentlyContext context)
	{
		LOGGER.info(" 当前处理的消息是 " + MessageHelp.GetMessagesToString(msgs));

		MessageExt msg = msgs.get(0);
		String strBody = null;
		try
		{
			// 直接在第一次 字节转换字符串的时候就指定为 UTF-8 编码
			strBody = new String(msg.getBody(), Encoding);
		}
		catch (Exception e)
		{
			LOGGER.error("获取数据为UTF-8格式出错    ", e);
			strBody = new String(msg.getBody());
		}

		LOGGER.info(MessageFormat.format("当前处理的消息 实体是  {0}  队列名称是 {1}  标签是{2} ", strBody,
				msg.getTopic(), msg.getTags()));

		// 消息处理次数的处理
		if (msg.getReconsumeTimes() > count)
		{
			LOGGER.info("容错次数超出  msg=" + msg);
			try
			{
				if (mqExceedCount != null) mqExceedCount.exceedCount(strBody, msg, context);
			}
			catch (Exception e)
			{
				LOGGER.info("   容错方法出现异常  ", e);
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		}
		return consumer.consumeMessage(strBody, msg, context);
	}
}
