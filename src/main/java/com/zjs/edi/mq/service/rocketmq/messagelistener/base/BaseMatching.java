package com.zjs.edi.mq.service.rocketmq.messagelistener.base;

import java.util.List;
import java.util.Map;

/**
* <p>Title: BaseMatching </p>
* <p>@Description: 验证规则数据源 获取接口   XML 配置大于数据源 </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月2日 上午10:04:21 
*/
public interface BaseMatching
{
	/**
	 * 获取 验证规则 数据
	 * 
	 * web服务调用 消费端：
	 * 		list
	 *    		map
	 *    			Tag =  test1,test2,test3
	 *    			body=  客户编码A，客户编码B
	 *    			url = http://10.10.12.27
	 * 转发消费端:
	 *     list
	 *    		map
	 *    			Tag =  test1,test2,test3
	 *    			body=  客户编码A，客户编码B
	 *    			Topic= top1,top2,top3
	 *    			Tags=t1||t2   或者  Tags=*   
	 * 
	 * @return
	 */
	List<Map<String, String>> getMatching();
}
