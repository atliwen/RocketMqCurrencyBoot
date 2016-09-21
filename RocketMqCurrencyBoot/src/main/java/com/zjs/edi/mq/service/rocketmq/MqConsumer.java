package com.zjs.edi.mq.service.rocketmq;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

/**
* <p>Title: MqConsumer </p>
* <p>@Description: 消费者类
* 		该对象是多例的    请手动 销毁 
*  
* 		配置文件中需要制定 
* 			MQ.NamesrvAddr 
* 			MQ.consumerGroup
* 		
*   	消费实体类是 DefaultMQPushConsumer*   
*   </p>
* <p>Company:  zjs </p>
* @author 李文
* @date   2016年8月1日 上午10:06:08 
*/
@Component
@Scope("prototype")
@Lazy(true)
public class MqConsumer
{
	private final Logger logger = LoggerFactory.getLogger(MqConsumer.class);

	/**
	 * MQ的PushConsumer 消费端 主体类
	 */
	private DefaultMQPushConsumer defaultMQPushConsumer;

	/**
	 * MQ地址
	 */
	@Value("${MQ.NamesrvAddr}")
	private String namesrvAddr;

	/**
	 *  消费端组名
	 */
	@Value("${MQ.consumerGroup}")
	private String consumerGroup;

	/**
	 * 实际消费类
	 */
	@Autowired
	private MessageListenerConcurrently defaultMessageListener;

	/**
	 * 启动一个消费端    
	 * @param Topic   队列名称
	 * @param Tags	  标签
	 * @throws Exception    		错误消息  
	 */
	public String init(String Topic, String Tags) throws Exception
	{
		return this.init(Topic, Tags, null, null);
	}

	/**
	 * 启动一个消费端    
	 * @param Topic   队列名称
	 * @param Tags	  标签
	 * @param consumeFromWhere      从哪里开始消费
	 * @param messageModel 			广播  / 聚集
	 * @throws Exception    		错误消息  
	 */
	public String init(String Topic, String Tags, ConsumeFromWhere consumeFromWhere,
			MessageModel messageModel) throws Exception
	{

		// 参数信息
		logger.info(MessageFormat
				.format("消费者 初始化!  consumerGroup={0}   namesrvAddr={1}  Topic={2}   Tags={3}  ConsumeFromWhere={4}  MessageModel={5} ",
						consumerGroup, namesrvAddr, Topic, Tags, consumeFromWhere, messageModel));

		// 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
		// 注意：ConsumerGroupName需要由应用来保证唯一
		defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
		defaultMQPushConsumer.setNamesrvAddr(namesrvAddr);
		defaultMQPushConsumer.setInstanceName(String.valueOf(System.currentTimeMillis()));

		// 订阅指定MyTopic下tags等于MyTag
		if (StringUtils.isEmpty(Topic)) throw new Exception("Topic 不能为空");
		if (StringUtils.isEmpty(Tags)) { throw new Exception("Tags 不能为空"); }

		defaultMQPushConsumer.subscribe(Topic, Tags);

		// 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
		// 如果非第一次启动，那么按照上次消费的位置继续消费
		if (consumeFromWhere == null)
		{
			consumeFromWhere = consumeFromWhere.CONSUME_FROM_FIRST_OFFSET;
		}
		defaultMQPushConsumer.setConsumeFromWhere(consumeFromWhere);

		if (messageModel == null)
		{
			messageModel = messageModel.CLUSTERING;
		}

		// 设置为集群消费(区别于广播消费)
		defaultMQPushConsumer.setMessageModel(messageModel);
		defaultMQPushConsumer.registerMessageListener(defaultMessageListener);

		// Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
		defaultMQPushConsumer.start();

		String clientID = defaultMQPushConsumer.getClientIP() + "@"
				+ defaultMQPushConsumer.getInstanceName();

		logger.info("消费者 " + clientID + "启动成功!");

		return clientID;
	}

	/**
	 *  设置多例后   Spring 将不负责销毁   需要手动销毁
	 */
	public void destroy()
	{

		logger.info("关闭消费者 开始");
		defaultMQPushConsumer.shutdown();
		logger.info("关闭消费者 结束");
	}
}
