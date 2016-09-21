package com.currencyboot.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>Title: OrApiTest </p>
* <p>@Description: 用于测试的 消费外调  Api 服务 </p>
* <p>Company:  </p>
* @author 李文
* @date   2016年8月2日 下午4:24:22 
*/
@RestController
public class OrApiTest
{

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<String> getEdiTest(@RequestParam("Topic") String Topic,
			@RequestParam("Tags") String Tags, @RequestParam("Body") String Body)
	{
		System.out.println(Body);
		// return
		// ResponseEntity.status(HttpStatus.ACCEPTED).body("处理完成  并且后续不转发");

		return ResponseEntity.ok("OK");
	}
}
