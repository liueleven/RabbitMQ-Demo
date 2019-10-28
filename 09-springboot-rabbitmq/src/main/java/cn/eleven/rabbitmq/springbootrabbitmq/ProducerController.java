package cn.eleven.rabbitmq.springbootrabbitmq;

import cn.eleven.rabbitmq.springbootrabbitmq.config.RabbitmqConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



/**
 * @description: 模拟生产者生产消息
 * @date: 2019-10-28 09:18
 * @author: 十一
 */
@Controller
@RequestMapping("/mq")
public class ProducerController{

    @Autowired
    private AmqpTemplate amqpTemplate;

    @RequestMapping(value = "/send",method = RequestMethod.GET)
    public void sendMsg(@RequestParam("content") String content) {
        //把消息放入ROUTINGKEY_A对应的队列当中去，对应的是队列A
        amqpTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_A,"",content);
    }


}
