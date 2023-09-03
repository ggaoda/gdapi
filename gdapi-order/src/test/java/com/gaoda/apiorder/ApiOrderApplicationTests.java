package com.gaoda.apiorder;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

import static com.gaoda.apiorder.config.RabbitmqConfig.EXCHANGE_ORDER_PAY;
import static com.gaoda.apiorder.config.RabbitmqConfig.ROUTINGKEY_ORDER_PAY;

@SpringBootTest
@Slf4j
@Log4j
class ApiOrderApplicationTests {

    @Resource
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {

        String msg="我是模拟死信队列的消息";
        rabbitTemplate.convertAndSend(EXCHANGE_ORDER_PAY,ROUTINGKEY_ORDER_PAY, msg, (message) -> {
            //设置有效时间，如果消息不被消费，进入死信队列
            message.getMessageProperties().setExpiration("1000");
            return message;
        });

    }

}