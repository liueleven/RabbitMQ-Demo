package cn.eleven.rabbitmq.springbootrabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;


/**
 * @description: 消费者 处理消息
 * @date: 2019-10-28 09:11
 * @author: 十一
 */
public class Consumer implements ChannelAwareMessageListener {


    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        System.out.println("监听到的消息：" + new String(message.getBody()));
    }
}
