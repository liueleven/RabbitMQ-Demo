package cn.eleven.rabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * @description: 一定要写注释啊
 * @date: 2020-01-12 17:30
 * @author: 十一
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitMQAdminTest {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testAdmin() {

        // 创建交换器
        rabbitAdmin.declareExchange(new DirectExchange("test.direct",false,false));

        rabbitAdmin.declareExchange(new TopicExchange("test.topic",false,false));

        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout",false,false));

        // 创建队列
        rabbitAdmin.declareQueue(new Queue("test.direct.queue",false));
        rabbitAdmin.declareQueue(new Queue("test.topic.queue",false));
        rabbitAdmin.declareQueue(new Queue("test.fanout.queue",false));

        // 交换器和队列进行绑定
        rabbitAdmin.declareBinding(new Binding("test.direct.queue"
                ,Binding.DestinationType.QUEUE
        ,"test.direct","direct",new HashMap<>()));

        // 方式二
        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue("test.topic.queue",false))
                .to(new TopicExchange("test.topic",false,false))
                .with("user.#")
        );

        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue("test.fanout.queue",false))
                        .to(new FanoutExchange("test.fanout",false,false))
        );

        // 删除队列
        rabbitAdmin.purgeQueue("test.topic.queue",false);
    }
}
