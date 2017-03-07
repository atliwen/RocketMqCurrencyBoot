package com.currencyboot.service.rocketmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 启动类
 *
 * @author 李文
 * @create 2017-03-07 13:27
 **/
@Component
public class MqConsumerStart implements CommandLineRunner
{

    @Autowired
    private MqConsumer consumer;

    @Autowired
    private HttpServletRequest request;

    @Value("${MQ.Topic:null}")
    private String Topic;
    @Value("${MQ.Tags:null}")
    private String Tags;


    @Override
    public void run(String... arg0) throws Exception {

        if (!"null".equals(Topic)) {
            String clientID;
            if (!"null".equals(Tags))
                consumer.init(Topic, Tags);
            else
                 consumer.init(Topic, "*");
        }

    }
}
