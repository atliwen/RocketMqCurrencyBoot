package com.zjs.edi.mq;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
* <p>Title: ConfigClass </p>
* <p>@Description: 配置文件载入类 </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月3日 上午11:40:28 
*/
@Configuration
@ImportResource(locations = "classpath:applicationContext-wd.xml")
public class ConfigClass
{

}
