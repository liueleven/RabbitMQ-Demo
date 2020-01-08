package cn.eleven.rabbitmq.quickstarter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @description: 一定要写注释啊
 * @date: 2019-12-14 08:12
 * @author: 十一
 */
public class Producer {

    public static void main(String[] args) throws Exception{
        // 配置rabbitmq
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        // 创建连接
        Connection connection = connectionFactory.newConnection();

        // 通过 connection 获取一个 channel
        Channel channel = connection.createChannel();

        // 通过 channel 发送数据

        for (int i=0; i<5; i++) {
            String msg = "Hello RabbitMQ";
            channel.basicPublish("","test001",null,msg.getBytes());
        }

        // 关闭连接
        channel.close();
        connection.close();

    }
}
