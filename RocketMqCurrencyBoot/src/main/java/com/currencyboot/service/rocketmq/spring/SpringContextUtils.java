package com.currencyboot.service.rocketmq.spring;

import java.util.Map;

import org.springframework.context.ApplicationContext;

/**
* <p>Title: SpringContextUtils </p>
* <p>@Description: spring 上下文获取类  </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月3日 上午11:43:18 
*/
public class SpringContextUtils
{

	public static void setApplicationContext(ApplicationContext arg0)
	{
		applicationContext = arg0;
	}

	public static ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	public static Object getBeanById(String id)
	{
		return applicationContext.getBean(id);
	}

	public static Object getBeanByClass(Class c)
	{
		return applicationContext.getBean(c);
	}

	public static Map getBeansByClass(Class c)
	{
		return applicationContext.getBeansOfType(c);
	}

	private static ApplicationContext applicationContext;

}
