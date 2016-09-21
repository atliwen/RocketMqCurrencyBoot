package com.currencyboot.service.rocketmq;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionMQProducer;
import com.alibaba.rocketmq.common.message.Message;

/**
* <p>Title: MqProducer </p>
* <p>@Description: 发送MQ 类  
* 		NamesrvAddr   
* 		ProducerGroupName
*  		InstanceName 
*  		SendMsgTimeout
*       需要在配置文件中设置 
*       
*       日志 建议单独配置 文件夹记录 方便问题排查
*       
*       备注：  该类是基于SPRING 管理的    请使用  SPRING    BAEN 是单例模式
*       
*       
*       PS: 该类不加入 没有设置注解标签 所有不会主动加入Spring 方法只使用 消费端 不使用  生产端 的环境
*  </p>
* <p>Company:  zjs </p>
* @author 李文
* @date   2016年8月1日 上午10:06:08 
*/
public class MqProducer
{
	Object producer = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(MqProducer.class);

	@Value("${MQ.NamesrvAddr}")
	private String NamesrvAddr;

	@Value("${MQ.ProducerGroupName}")
	private String ProducerGroupName;

	@Value("${MQ.InstanceName}")
	private String InstanceName;

	@Value("${MQ.SendMsgTimeout:20000}")
	private int SendMsgTimeout;

	/**
	 * Broker 回查 Producer 事务状态时， 线程池大小 
	 */
	@Value("${MQ.checkThreadPoolMinSize:1}")
	private int checkThreadPoolMinSize;

	/**
	 * Broker 回查 Producer 事务状态时， 线程池大小 
	 */
	@Value("${MQ.checkThreadPoolMaxSize:1}")
	private int checkThreadPoolMaxSize;

	/**
	 * Broker 回查 Producer 事务状态时，Producer 本地缓冲请求队列大小
	 */
	@Value("${MQ.checkRequestHoldMax:2000}")
	private int checkRequestHoldMax;

	/**
	 * 未决事务，服务器回查客户端
	 */
	@Autowired(required = false)
	// @Qualifier("transactionCheckListener")
	private TransactionCheckListener transaction;

	/**
	 * 本地事务
	 */
	@Autowired(required = false)
	// @Qualifier("localTransactionExecuter")
	private LocalTransactionExecuter transactionExecuter;

	@PostConstruct
	private void init() throws MQClientException
	{
		if (transaction == null || transactionExecuter == null)
		{
			DefaultMQProducer defaultProducer = new DefaultMQProducer();
			// Producer 组名， 多个 Producer 如果属于一 个应用，发送同样的消息，则应该将它们 归为同一组
			defaultProducer.setProducerGroup(ProducerGroupName);
			// Name Server 地址列表
			defaultProducer.setNamesrvAddr(NamesrvAddr);
			// 生产者名称
			defaultProducer.setInstanceName(InstanceName);
			// 超时时间
			defaultProducer.setSendMsgTimeout(SendMsgTimeout);
			defaultProducer.start();
			producer = defaultProducer;
		}
		else
		{
			TransactionMQProducer transactionProducer = new TransactionMQProducer();
			// Producer 组名， 多个 Producer 如果属于一 个应用，发送同样的消息，则应该将它们 归为同一组
			transactionProducer.setProducerGroup(ProducerGroupName);
			// Name Server 地址列表
			transactionProducer.setNamesrvAddr(NamesrvAddr);
			// 生产者名称
			transactionProducer.setInstanceName(InstanceName);
			// 超时时间
			transactionProducer.setSendMsgTimeout(SendMsgTimeout);
			transactionProducer.setCheckThreadPoolMinSize(checkThreadPoolMinSize);
			transactionProducer.setCheckThreadPoolMaxSize(checkThreadPoolMaxSize);
			transactionProducer.setCheckRequestHoldMax(checkRequestHoldMax);
			transactionProducer.setTransactionCheckListener(transaction);
			transactionProducer.start();
			producer = transactionProducer;
		}

	}

	@PreDestroy
	private void Destroy()
	{
		if (producer instanceof DefaultMQProducer)
		{
			DefaultMQProducer producerMq = (DefaultMQProducer) producer;
			if (producerMq != null)
			{
				producerMq.shutdown();
			}
		}
	}

	/**
	 *  发送数据到MQ方法    数据编码格式  默认UTF-8
	 * 
	 * @param Topic   队列名称
	 * @param Tags	     标签名称
	 * @param body	  发送的数据  推荐 JSOM 或者 XML 结构
	 * @return   响应信息进行了内部处理  确认已经保存到 MQ 并且    日志已经记录  只要值不是NULL 就是成功发送
	 * @throws UnsupportedEncodingException  转换字符集出错  请检查是否可以转换 
	 */
	public SendResult send(String Topic, String Tags, String body)
			throws UnsupportedEncodingException
	{
		return this.send(Topic, Tags, body, null);
	}

	/**
	 *  发送数据到MQ方法  
	 * 
	 * @param Topic   队列名称
	 * @param Tags	     标签名称
	 * @param body	  发送的数据  推荐 JSOM 或者 XML 结构
	 * @param Encoding  数据编码格式  默认UTF-8
	 * @return   响应信息进行了内部处理  确认已经保存到 MQ 并且    日志已经记录  只要值不是NULL 就是成功发送
	 * @throws UnsupportedEncodingException  转换字符集出错  请检查是否可以转换 
	 */
	public SendResult send(String Topic, String Tags, String body, String Encoding)
			throws UnsupportedEncodingException
	{

		String loggerString = MessageFormat.format(
				"将要发送到Mq的数据    Topic={0}   Tags={1}   body={2}  Encoding={3} ", Topic, Tags, body,
				Encoding);

		if (Encoding == null || "".equals(Encoding))
		{
			Encoding = "UTF-8";
		}

		if (Tags == null || "".equals(Tags))
		{
			Tags = "*";
		}

		LOGGER.info(loggerString);

		Message me = new Message();
		// 标示
		me.setTopic(Topic);
		// 标签
		me.setTags(Tags);
		// 内容
		me.setBody(body.getBytes(Encoding));
		// 发送信息到MQ SendResult 是当前发送的状态 官方说 不出异常 就是成功
		SendResult sendResult = null;
		try
		{
			if (producer instanceof TransactionMQProducer)
			{
				sendResult = ((TransactionMQProducer) producer).sendMessageInTransaction(me,
						transactionExecuter, null);

			}
			else
			{
				sendResult = ((DefaultMQProducer) producer).send(me);
			}
		}
		catch (Exception e)
		{
			LOGGER.error(" 发送 数据给MQ出现异常  " + loggerString, e);
		}
		// 当消息发送失败时如何处理 getSendStatus 获取发送的状态
		if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK)
		{
			LOGGER.info(loggerString + "发送消息失败" + " MQ状态值  SendResult=" + sendResult);
			sendResult = null;
		}
		LOGGER.info("发送到MQ成功" + sendResult);
		return sendResult;
	}

	// ---- set -----

	public void setNamesrvAddr(String namesrvAddr)
	{
		NamesrvAddr = namesrvAddr;
	}

	public void setProducerGroupName(String producerGroupName)
	{
		ProducerGroupName = producerGroupName;
	}

	public void setInstanceName(String instanceName)
	{
		InstanceName = instanceName;
	}

	public void setSendMsgTimeout(int sendMsgTimeout)
	{
		SendMsgTimeout = sendMsgTimeout;
	}

}
