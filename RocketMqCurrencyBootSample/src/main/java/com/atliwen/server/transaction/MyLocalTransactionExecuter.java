package com.atliwen.server.transaction;

import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.common.message.Message;

/**
* <p>Title: MyLocalTransactionExecuter </p>
* <p>@Description: 本地事务 实例 </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月5日 下午3:22:00 
*/
public class MyLocalTransactionExecuter implements LocalTransactionExecuter
{

	@Override
	public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg)
	{
		System.out.println(" 本地事务 " + msg);
		return LocalTransactionState.COMMIT_MESSAGE;
	}

}
