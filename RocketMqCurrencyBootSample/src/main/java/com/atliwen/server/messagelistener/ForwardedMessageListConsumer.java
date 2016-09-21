package com.atliwen.server.messagelistener;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.currencyboot.service.rocketmq.MqProducer;
import com.currencyboot.service.rocketmq.common.ForwardedHelp;
import com.currencyboot.service.rocketmq.messagelistener.Interface.ForwardedMessageListConsumerInterface;
import com.currencyboot.service.rocketmq.messagelistener.Interface.MatchingInterface;
import com.currencyboot.service.rocketmq.messagelistener.Interface.MessageListenerConsumerInterface;

/**
* <p>Title: ForwardedMessageListConsumer </p>
* <p>@Description: 转发消费端  </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月1日 上午10:11:57 
*/
public class ForwardedMessageListConsumer implements MessageListenerConsumerInterface
{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ForwardedMessageListConsumer.class);

	/**
	 *    验证规则
	 *    
	 *    list
	 *    		map
	 *    			Tag =  test1,test2,test3
	 *    			body=  客户编码A，客户编码B
	 *    			Topic= top1,top2,top3
	 *    			Tags=t1||t2   或者  Tags=*
	 *    	
	 */
	private List<Map<String, String>> matching;

	/**
	 *  生产端
	 */
	private MqProducer producer;

	/**
	 * 验证规则数据源获取接口
	 */
	private MatchingInterface baseMatching;

	/**
	 * 转发 消息处理 
	 */
	private ForwardedMessageListConsumerInterface forwarded;

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(String strBody, MessageExt msg,
			ConsumeConcurrentlyContext context)
	{
		if (baseMatching != null)
		{
			List<Map<String, String>> me = baseMatching.getMatching();
			if (me != null && me.size() != 0) matching = me;
		}

		for (Map<String, String> map : matching)
		{
			if (forwarded != null) strBody = forwarded.MessageConsumer(strBody, msg, context);
			return sendMqTags(map, msg.getTags(), strBody);
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 *    验证规则 并按照规则将数据 转发到对应的 队列中
	 */
	private ConsumeConcurrentlyStatus sendMqTags(Map<String, String> matchingMap, String MqTags,
			String Mqbody)
	{
		// 验证是否有空值
		String[] keys =
		{ "Tag", "body", "Topic", "Tags" };

		try
		{
			ForwardedHelp.outStr(matchingMap, keys);
		}
		catch (Exception e)
		{
			LOGGER.error(" 转发消费端  验证规则 并按照规则将数据 转发到对应的 ", e.getMessage());
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}

		return equalsTag(matchingMap, MqTags, Mqbody);

	}

	/**
	 *   匹配 Tag 消息 Tag 的验证
	 *   
	 * @param matchingMap   注入的  匹配 规则 数据
	 * @param MqTags        当前消费的MQ  Tags
	 * @param Mqbody		当前转发的 消息 实体
	 * @param Topic			需要转发到的 MQ Topic
	 * @return
	 */
	private ConsumeConcurrentlyStatus equalsTag(Map<String, String> matchingMap, String MqTags,
			String Mqbody)
	{
		String[] Topic = matchingMap.get("Topic").split(",");

		// 是否需要 匹配 Tag
		if ("*".equals(matchingMap.get("Tag")))
		{
			return equalsbody(matchingMap, MqTags, Mqbody, Topic);// 不需要匹配body
		}
		else
		{
			// 进行匹配 Tag
			if (ForwardedHelp.isContains(MqTags, matchingMap.get("Tag")))
			{
				// 匹配成功 进行 匹配 body
				return equalsbody(matchingMap, MqTags, Mqbody, Topic);
			}
			else
			{
				LOGGER.debug("Tag 匹配未成功 放弃该消息    消息内容是   " + MqTags);
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		}
	}

	/**
	 *   匹配 body 消息实体的验证
	 *   
	 * @param matchingMap   注入的  匹配 规则 数据
	 * @param MqTags        当前消费的MQ  Tags
	 * @param Mqbody		当前转发的 消息 实体
	 * @param Topic			需要转发到的 MQ Topic
	 * @return
	 */
	private ConsumeConcurrentlyStatus equalsbody(Map<String, String> matchingMap, String MqTags,
			String Mqbody, String[] Topic)
	{
		// 匹配 body
		if ("*".equals(matchingMap.get("body")))
		{
			// 不需要 匹配 Tag
			return sendMq(matchingMap, Mqbody, Topic);
		}

		if (!ForwardedHelp.isContains(Mqbody, matchingMap.get("body")))
		{
			LOGGER.debug("body 匹配未成功 放弃该消息    消息内容是   " + MqTags);
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		}
		else
		{
			// 转发到MQ
			return sendMq(matchingMap, Mqbody, Topic);
		}
	}

	/**
	 *  将数据发送给MQ 
	 * @param matchingMap   注入的配置数据
	 * @param Mqbody	当前消息的 实体
	 * @param Topic		转发的队列名称
	 */
	private ConsumeConcurrentlyStatus sendMq(Map<String, String> matchingMap, String Mqbody,
			String[] Topic)
	{
		for (String topicval : Topic)
		{
			try
			{
				if (producer.send(topicval, matchingMap.get("Tags"),
						Mqbody) == null) { return ConsumeConcurrentlyStatus.RECONSUME_LATER; }
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			catch (UnsupportedEncodingException e)
			{
				LOGGER.error(" 转发消费端 异常 转发消息到MQ  失败  Tag= " + matchingMap.get("Topic") + " body= "
						+ Mqbody, e);
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**  
	 * 设置验证规则listmapTag=test1test2test3body=客户编码A，客户编码BTopic=top1top2top3Tags=t1||t2或者Tags=  
	 * @param matching 验证规则listmapTag=test1test2test3body=客户编码A，客户编码BTopic=top1top2top3Tags=t1||t2或者Tags=  
	 */
	public void setMatching(List<Map<String, String>> matching)
	{
		this.matching = matching;
	}

	/**  
	 * 设置生产端  
	 * @param producer 生产端  
	 */
	public void setProducer(MqProducer producer)
	{
		this.producer = producer;
	}

	/**  
	 * 设置转发消息处理  
	 * @param forwarded 转发消息处理  
	 */
	public void setForwarded(ForwardedMessageListConsumerInterface forwarded)
	{
		this.forwarded = forwarded;
	}

	/**
	 * @param 验证规则数据源获取接口 the baseMatching to set
	 */
	public void setBaseMatching(MatchingInterface baseMatching)
	{
		this.baseMatching = baseMatching;
	}

}
