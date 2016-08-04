package com.zjs.edi.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

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

		final ApplicationContext applicationContext = SpringApplication
				.run(Application.class, args);
		SpringContextUtils.setApplicationContext(applicationContext);
	}
}
