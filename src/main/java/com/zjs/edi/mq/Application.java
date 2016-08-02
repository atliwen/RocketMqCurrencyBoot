package com.zjs.edi.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.zjs.edi.mq.service.rocketmq.spring.SpringContextUtils;

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
