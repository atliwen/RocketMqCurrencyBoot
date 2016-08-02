package com.zjs.edi.mq.web;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zjs.edi.mq.domain.mq.AttributeNames;
import com.zjs.edi.mq.service.rocketmq.MqConsumer;
import com.zjs.edi.mq.service.rocketmq.spring.SpringContextUtils;

/**
* <p>Title: MqConsumerController </p>
* <p>@Description:  MqConsumer 消费端 控制类 </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年5月20日 下午7:21:12 
*/
@RequestMapping("rest/MqConsumer")
@RestController
@CrossOrigin
public class MqConsumerController
{

	private static final Logger LOGGER = LoggerFactory.getLogger(MqConsumerController.class);

	@Autowired
	private HttpServletRequest request;

	/**
	 *   
	 *   开启一个 消费端     
	 *      该方法会创建一个 消费端类 ，该类 将添加到servletContext 进行管理    
	 *   重要提示 每次调用改方法都会 开启一个新的消费端   
	 *   
	 * @param Topic   Topic
	 * @param Tags	Tag     标签可以 定 未获取多个    具体请看MQ 文档
	 * @return  是否成功开启
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> startMqConsumerTag(@RequestParam("Topic") String Topic,
			@RequestParam("Tags") String Tags)
	{
		MqConsumer consumer;
		String clientID;
		try
		{
			consumer = (MqConsumer) SpringContextUtils.getBeanByClass(MqConsumer.class);
			if (Tags == null || "".equals(Tags)) Tags = "*";
			clientID = consumer.init(Topic, Tags);
		}
		catch (Exception e)
		{
			LOGGER.error("MqConsumerController 启动消费端异常  ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		ServletContext servletContext = request.getServletContext();

		if (servletContext.getAttribute(clientID) == null)
		{
			servletContext.setAttribute(clientID, consumer);
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	/**
	 *  销毁一个消费端 
	 *  
	 *     该方法  会通过传递的   ConsumerID 从 servletContext 查询 是否有 该对象 
	 *     然后调用 其销毁方法 
	 * 
	 * @param ConsumerID    消费者唯一标识   
	 * @return   
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<String> destroyMqConsumer(@RequestParam("Consumer") String Consumer)
	{

		ServletContext servletContext = request.getServletContext();

		Enumeration<String> attributeNames = servletContext.getAttributeNames();

		List<String> outList = new ArrayList<String>();

		Object AttributeConsumer = servletContext.getAttribute(Consumer);
		if (AttributeConsumer != null)
		{
			MqConsumer mqConsumer = (MqConsumer) AttributeConsumer;
			mqConsumer.destroy();
			servletContext.setAttribute(Consumer, null);

			return ResponseEntity.status(HttpStatus.OK).body(Consumer + " 消费者销毁成功");
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Consumer + " 未找到   该消费者 ");
	}

	/**
	 * 获取servletContext 中存放的 所有  消费者对象 唯一标识 
	 * 
	 * @return  返回  说有消费者唯一标识  
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET)
	public List<AttributeNames> queryMqConsumerList()
	{
		ServletContext servletContext = request.getServletContext();
		List<AttributeNames> list = new ArrayList<AttributeNames>();
		Enumeration<String> attributeNames = servletContext.getAttributeNames();
		while (attributeNames.hasMoreElements())
		{
			String nameString = (String) attributeNames.nextElement();
			if (nameString.contains("@"))
			{
				AttributeNames attri = new AttributeNames();
				attri.setKid(nameString);
				list.add(attri);
			}
		}
		return list;
	}

}
