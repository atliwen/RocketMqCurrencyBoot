package com.currencyboot.web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

import com.alibaba.rocketmq.client.producer.SendResult;
import com.currencyboot.service.rocketmq.MqProducer;

/**
* <p>Title: MqProducerController </p> 
* <p>@Description: 生产端    </p> 
* <p>Company:  </p> 
* @author 李文  
* @date   2016年5月20日 下午7:32:25 
*/
@RequestMapping("rest/MqProducer")
@RestController
@CrossOrigin
public class MqProducerController
{

	private static final Logger LOGGER = LoggerFactory.getLogger(MqProducerController.class);

	/**
	 *  消费端生产类 
	 */
	@Autowired(required = false)
	private MqProducer mqProducer;

	/**
	 *    添加一个消息到MQ 
	 *      使用该方法 请注意  Topic  Tags  参数     
	 *      
	 * @param Topic   队列名称
	 * @param Tags	     标签名称  
	 * @param body    实际数据
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<List<String>> controllerMq(@RequestParam("Topic") String Topic,
			@RequestParam("Tags") String Tags, @RequestParam("body") String body)
	{
		List<String> list = new ArrayList<String>();
		try
		{
			String getRequestData = "接收到的消息是   Topic=" + Topic + " Tags= " + Tags + " body= "
					+ body;

			LOGGER.debug(getRequestData);

			SendResult sendResult = mqProducer.send(Topic, Tags, body);
			LOGGER.debug("响应的数据是：" + sendResult);
			if (null != sendResult)
			{
				list.add("SendStatus = " + sendResult.getSendStatus() + " msgId ="
						+ sendResult.getMsgId());
				return ResponseEntity.ok(list);
			}
			list.add(" 添加到MQ 失败 ！");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(list);
		}
		catch (UnsupportedEncodingException e)
		{
			LOGGER.error("MqProducerController 发送异常", e);
			list.add(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(list);
		}
	}
}
