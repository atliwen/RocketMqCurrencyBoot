/**
 * 
 */
package com.zjs.edi.mq.service.rocketmq.common;

import java.util.Map;

/**
* <p>Title: ForwardedHelp </p>
* <p>@Description: 匹配规则 校验类  </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月1日 上午10:06:08 
*/
public class ForwardedHelp
{
	/**
	 *  空值的验证    
	 * 
	 * @param matchingMap  注入数据
	 * @param keys		   需要 验证的 MAP key
	 * @throws Exception  有空值 或者  “” 抛出异常
	 */
	public static void outStr(Map<String, String> matchingMap, String[] keys) throws Exception
	{
		for (String key : keys)
		{
			String val = matchingMap.get(key);
			if (val == null || "".equals(val)) { throw new Exception(" 验证规则 有空或者为配置    Key ： "
					+ val); }
		}
	}

	/**
	 *  匹配验证
	 * 
	 * @param MapVal     验证规则的值 
	 * @param key			被验证的数据
	 * @return
	 */
	public static boolean isContains(String MapVal, String key)
	{

		if (MapVal == null && "".equals(MapVal)) { return false; }
		String[] val = key.split(",");
		if (val == null) { return false; }
		for (String string : val)
		{
			if (MapVal.contains(string.trim())) { return true; }
		}
		return false;
	}

}
