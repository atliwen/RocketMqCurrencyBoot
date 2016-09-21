package com.atliwen.server.transaction;

import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.common.message.MessageExt;

/**
* <p>Title: MyTransactionCheckListener </p>
* <p>@Description: 未决事务，服务器回查客户端    示例 </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月5日 下午3:20:35 
*/
public class MyTransactionCheckListener implements TransactionCheckListener
{

	@Override
	public LocalTransactionState checkLocalTransactionState(MessageExt msg)
	{
		System.out.println(" 未决事务   " + msg);
		return LocalTransactionState.COMMIT_MESSAGE;
	}

}
