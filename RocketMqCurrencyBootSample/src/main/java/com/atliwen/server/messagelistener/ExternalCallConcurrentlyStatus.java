package com.atliwen.server.messagelistener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.currencyboot.domain.mq.HttpResponse;
import com.currencyboot.service.rocketmq.MqProducer;
import com.currencyboot.service.rocketmq.common.ForwardedHelp;
import com.currencyboot.service.rocketmq.common.HttpRequest;
import com.currencyboot.service.rocketmq.messagelistener.Interface.ExternalCallConsumerInterface;
import com.currencyboot.service.rocketmq.messagelistener.Interface.MatchingInterface;
import com.currencyboot.service.rocketmq.messagelistener.Interface.MessageListenerConsumerInterface;

/**
* <p>Title: ExternalCallConcurrentlyStatus </p>
* <p>@Description: web服务调用 消费端  </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月1日 上午10:09:59 
*/
public class ExternalCallConcurrentlyStatus implements MessageListenerConsumerInterface
{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExternalCallConcurrentlyStatus.class);

	/**
	 *    验证规则
	 *    
	 *    list
	 *    		map
	 *    			Tag =  test1,test2,test3
	 *    			body=  客户编码A，客户编码B
	 *    			url = http://10.10.12.27
	 *    	
	 */
	private List<Map<String, String>> matching;

	/**
	 *  数据字符集
	 */
	@Value("${MQ.Encoding:UTF-8}")
	private String Encoding;

	/**
	 *  转译  实体数据
	 */
	private ExternalCallConsumerInterface externalCall;

	/**
	 * 验证规则数据源获取接口
	 */
	private MatchingInterface baseMatching;

	/**
	 * 转发模式
	 */
	private ForwardedMessageListConsumer forwarded;

	/**
	 *  生产端
	 */
	@Autowired(required = false)
	private MqProducer producer;

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(String strBody, MessageExt msg,
			ConsumeConcurrentlyContext context)
	{

		if (baseMatching != null)
		{
			List<Map<String, String>> me = baseMatching.getMatching();
			if (me != null && me.size() != 0) matching = me;
		}

		// TODO 待完善 日志系统 最简单的方法是 使用 mongodb 存放日志数据
		for (Map<String, String> map : matching)
		{
			Map<String, String> params = new HashMap<String, String>();

			params.put("Topic", msg.getTopic());
			params.put("Tags", msg.getTags());

			if (externalCall == null) params.put("Body", strBody);
			else params.put("Body", externalCall.MessageConsumer(strBody, msg, context));

			return sendMqTags(map, msg.getTags(), params, strBody, msg, context);
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

	}

	/**
	 *    验证规则 并按照规则将数据 转发到对应的 队列中  
	 */
	public ConsumeConcurrentlyStatus sendMqTags(Map<String, String> matchingMap, String MqTags,
			Map<String, String> params, String strBody, MessageExt msg,
			ConsumeConcurrentlyContext context)
	{
		// 验证是否有空值
		String[] keys =
		{ "url", "Tag", "body" };
		try
		{
			ForwardedHelp.outStr(matchingMap, keys);
		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage());
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}

		return forwardedWebDate(matchingMap, MqTags, params, msg, context);

	}

	/**
	 * 外调 Web API 后续结果 继续转发       转发类  权级 》 XML 配置权级
	 * @param matchingMap
	 * @param MqTags
	 * @param params
	 * @param msg
	 * @param context
	 * @return
	 */
	private ConsumeConcurrentlyStatus forwardedWebDate(Map<String, String> matchingMap,
			String MqTags, Map<String, String> params, MessageExt msg,
			ConsumeConcurrentlyContext context)
	{
		HttpResponse response = equalsTag(matchingMap, MqTags, params);
		if (response.getState() == 202) { return ConsumeConcurrentlyStatus.CONSUME_SUCCESS; }
		if (response.getState() != 200) { return ConsumeConcurrentlyStatus.RECONSUME_LATER; }
		if (forwarded != null) { return forwarded.consumeMessage(response.getData(), msg,
				context); }

		String[] keyszf =
		{ "Topic", "Tags" };
		try
		{
			ForwardedHelp.outStr(matchingMap, keyszf);
		}
		catch (Exception e)
		{
			// LOGGER.debug(" 外调Web API 后续不进行转发 消息 body= " +
			// response.getData());
			LOGGER.error("外调Web API 后续不进行转发 消息 body= " + response.getData() + " " + e.getMessage());
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}

		try
		{
			if (producer == null)
			{
				LOGGER.error("  外调后续转发 发送失败   ， 并未配置  生产者 ");
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

			SendResult se = producer.send(matchingMap.get("Topic"), matchingMap.get("Tags"),
					response.getData());
			if (se == null)
			{
				LOGGER.error(" 外调后续转发 发送失败 。需要转发的数据   Topic=" + matchingMap.get("Topic") + "  Tags="
						+ matchingMap.get("Tags") + "  data" + response.getData());
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}
		}
		catch (Exception e)
		{

			LOGGER.error(" 外调 异常 转发消息到MQ  失败  Tag= " + matchingMap.get("Topic") + " body= "
					+ response.getData(), e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
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
	private HttpResponse equalsTag(Map<String, String> matchingMap, String MqTags,
			Map<String, String> params)
	{
		String url = matchingMap.get("url");

		// 是否需要 匹配 Tag
		if ("*".equals(matchingMap.get("Tag")))
		{
			// 不需要匹配
			// 匹配 body
			return equalsbody(matchingMap, MqTags, url, params);

		}
		else
		{
			// 进行匹配 Tag
			if (ForwardedHelp.isContains(MqTags, matchingMap.get("Tag")))
			{
				// 匹配成功 进行 匹配 body
				return equalsbody(matchingMap, MqTags, url, params);
			}
			else
			{
				LOGGER.debug("Tag 匹配未成功 放弃该消息    消息内容是   " + MqTags);
				return new HttpResponse(202, "body Tag 匹配未成功 放弃该消息");
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
	private HttpResponse equalsbody(Map<String, String> matchingMap, String MqTags, String url,
			Map<String, String> params)
	{
		// 匹配 body
		if ("*".equals(matchingMap.get("body")))
		{
			// 不需要 匹配 Tag
			return sendMq(matchingMap, url, params);
		}

		if (!ForwardedHelp.isContains(params.get("body"), matchingMap.get("body")))
		{
			LOGGER.debug("body 匹配未成功 放弃该消息    消息内容是   " + MqTags);
			return new HttpResponse(200, "body 匹配未成功 放弃该消息");
		}
		else
		{
			// 不需要 匹配 Tag
			return sendMq(matchingMap, url, params);
		}
	}

	/**
	 * @param matchingMap
	 * @param mqbody
	 * @param url
	 * @return
	 */
	private HttpResponse sendMq(Map<String, String> matchingMap, String url,
			Map<String, String> params)
	{

		return HttpRequest.sendPostMessage(url, params, Encoding);
	}

	/**  
	 * 设置验证规则listmapTag=test1test2test3body=客户编码A，客户编码Burl  
	 * @param matching 验证规则listmapTag=test1test2test3body=客户编码A，客户编码Burl  
	 */
	public void setMatching(List<Map<String, String>> matching)
	{
		this.matching = matching;
	}

	/**  
	 * 设置转译实体数据  
	 * @param externalCall 转译实体数据  
	 */
	public void setExternalCall(ExternalCallConsumerInterface externalCall)
	{
		this.externalCall = externalCall;
	}

	/**
	 * @param 转发模式 the forwarded to set
	 */
	public void setForwarded(ForwardedMessageListConsumer forwarded)
	{
		this.forwarded = forwarded;
	}

	/**  
	 * 设置验证规则数据源获取接口  
	 * @param baseMatching 验证规则数据源获取接口  
	 */
	public void setBaseMatching(MatchingInterface baseMatching)
	{
		this.baseMatching = baseMatching;
	}

}
