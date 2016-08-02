/**
 * 
 */
package com.zjs.edi.mq.service.rocketmq.messagelistener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.zjs.edi.mq.domain.mq.HttpResponse;
import com.zjs.edi.mq.service.rocketmq.common.ForwardedHelp;
import com.zjs.edi.mq.service.rocketmq.common.HttpRequest;
import com.zjs.edi.mq.service.rocketmq.messagelistener.base.BaseExternalCallConsumer;
import com.zjs.edi.mq.service.rocketmq.messagelistener.base.BaseMatching;
import com.zjs.edi.mq.service.rocketmq.messagelistener.base.BaseMessageListenerConsumer;

/**
* <p>Title: ExternalCallConcurrentlyStatus </p>
* <p>@Description: web服务调用 消费端 </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月1日 上午10:09:59 
*/
public class ExternalCallConcurrentlyStatus implements BaseMessageListenerConsumer
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
	 *  转译  实体数据
	 */
	private BaseExternalCallConsumer externalCall;

	/**
	 * 验证规则数据源获取接口
	 */
	private BaseMatching baseMatching;

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(String strBody, MessageExt msg,
			ConsumeConcurrentlyContext context)
	{

		if (baseMatching != null)
		{
			List<Map<String, String>> me = baseMatching.getMatching();
			if (me != null && me.size() != 0) matching = me;
		}

		// TODO 待完善 日志系统
		for (Map<String, String> map : matching)
		{
			Map<String, String> params = new HashMap<String, String>();

			params.put("Topic", msg.getTopic());
			params.put("Tags", msg.getTags());

			if (externalCall == null) params.put("Body", strBody);
			else params.put("Body", externalCall.MessageConsumer(strBody, msg, context));

			return sendMqTags(map, msg.getTags(), params);
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

	}

	/**
	 *    验证规则 并按照规则将数据 转发到对应的 队列中
	 */
	public static ConsumeConcurrentlyStatus sendMqTags(Map<String, String> matchingMap,
			String MqTags, Map<String, String> params)
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
		// TODO 待 增加 转发MQ 功能
		return equalsTag(matchingMap, MqTags, params);
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
	private static ConsumeConcurrentlyStatus equalsTag(Map<String, String> matchingMap,
			String MqTags, Map<String, String> params)
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
	private static ConsumeConcurrentlyStatus equalsbody(Map<String, String> matchingMap,
			String MqTags, String url, Map<String, String> params)
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
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
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
	private static ConsumeConcurrentlyStatus sendMq(Map<String, String> matchingMap, String url,
			Map<String, String> params)
	{

		HttpResponse resp = HttpRequest.sendPostMessage(url, params, "UTF-8");
		if (resp == null || resp.getState() != 200) { return ConsumeConcurrentlyStatus.RECONSUME_LATER; }
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
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
	public void setExternalCall(BaseExternalCallConsumer externalCall)
	{
		this.externalCall = externalCall;
	}

}
