package com.atliwen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.atliwen.server.messagelistener.ForwardedMessageListConsumer;
import com.atliwen.server.messagelistener.MqExceedCount;
import com.atliwen.server.translation.myForwardedMessageListConsumer;
import com.zjs.edi.mq.RocketMqCurrencyBoot;
import com.zjs.edi.mq.service.rocketmq.MqProducer;
import com.zjs.edi.mq.service.rocketmq.messagelistener.Interface.MessageListenerConsumerInterface;
import com.zjs.edi.mq.service.rocketmq.messagelistener.Interface.MqExceedCountInterface;
import com.zjs.edi.mq.service.rocketmq.spring.SpringContextUtils;

/**
 * <p>Title: Application </p>
 * <p>@Description: 启动入口类 </p>
 * <p>Company:  </p>
 * @author 李文
 * @date   2016年8月3日 上午11:40:10  
 */
@SpringBootApplication
public class Application
{
	public static void main(String[] args)
	{
		// SpringApplication.run 加载两个位置 当前类 位置 和 RocketMqCurrencyBoot 类 位置
		// 加载Spring

		// 如果 RocketMqCurrencyBoot 包名称 包括在 当前项目中 则 RocketMqCurrencyBoot.class
		// 不需要

		final ApplicationContext applicationContext = SpringApplication.run(new Object[]
		{ RocketMqCurrencyBoot.class, Application.class }, args);
		SpringContextUtils.setApplicationContext(applicationContext);

	}

	/**
	 * 构建 发送MQ 类   Bean
	 * @return
	 */
	@Bean
	MqProducer mqProducer()
	{
		return new MqProducer();
	}

	// ------------内销 模式 开始-----------------------

	// @Bean
	// MessageListenerConsumerInterface consumableMessageListenerConsumer()
	// {
	// return new ConsumableMessageListenerConsumer();
	// }

	// ------------内销 模式 结束-----------------------

	// -------------外调 模式 开始-----------------------

	// /**
	// * 构建 web服务调用 消费端
	// * @return
	// */
	// @Bean()
	// MessageListenerConsumerInterface externalCallConcurrentlyStatus()
	// {
	// ExternalCallConcurrentlyStatus e = new ExternalCallConcurrentlyStatus();
	// List<Map<String, String>> matching = new ArrayList<Map<String,
	// String>>();
	//
	// // 设置验证规则
	// Map<String, String> rule = new HashMap<String, String>();
	// rule.put("Tag", "a");
	// rule.put("body", "*");
	// rule.put("url", "http://10.10.12.27:8080");
	// rule.put("Topic", "orTest");
	// rule.put("Tags", "b");
	// e.setMatching(matching);
	// // 外调模式 消息加工处理类
	// e.setExternalCall(new myExternalCallConsumer());
	// // e.setBaseMatching(baseMatching); 这个方法是 希望通过 数据源来获取 匹配规则
	// return e;
	// }

	// -------------外调 模式 结束-----------------------

	// -------------转发 模式 开始-----------------------
	@Bean()
	MessageListenerConsumerInterface forwardedMessageListConsumer()
	{
		ForwardedMessageListConsumer f = new ForwardedMessageListConsumer();
		f.setProducer(new MqProducer());
		f.setForwarded(new myForwardedMessageListConsumer());
		List<Map<String, String>> matching = new ArrayList<Map<String, String>>();
		Map<String, String> rule = new HashMap<String, String>();
		rule.put("Tag", "a");
		rule.put("body", "a");
		rule.put("Topic", "orTest");
		rule.put("Tags", "b");
		f.setMatching(matching);
		// f.setBaseMatching(); 这个方法是 希望通过 数据源来获取 匹配规则
		return f;
	}
	// -------------转发 模式 结束-----------------------

	// -------------消费超出 设置的容错 次数 处理 类 开始------
	@Bean
	MqExceedCountInterface mqExceedCount()
	{
		// 该类不是非构建 类 不设置 消费次数超出后 自动成功。
		return new MqExceedCount();
	}

	// -------------消费超出 设置的容错 次数 处理 类 结束 ------

	// ------------事务 开始--------------------------

	// PS 当前MQ 事务支持非常不好 没啥用的赶脚 单个事务 还得等回调处理

	// /**
	// * 构建 本地事务 处理类
	// * @return
	// */
	// @Bean
	// LocalTransactionExecuter myLocalTransactionExecuter()
	// {
	// return new MyLocalTransactionExecuter();
	// }
	//
	// /**
	// * 构建 未决事务，服务器回查 处理类
	// */
	// @Bean
	// TransactionCheckListener myTransactionCheckListener()
	// {
	// return new MyTransactionCheckListener();
	// }

	// ------------事务 结束--------------------------

}
