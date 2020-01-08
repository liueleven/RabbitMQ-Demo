package cn.eleven.rabbitmq.quickstarter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;

/**
 * @description: 一定要写注释啊
 * @date: 2019-12-14 08:12
 * @author: 十一
 */
public class Consumer {

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

        /**
         * 声明一个队列
         * @param queue the name of the queue
         * @param durable true if we are declaring a durable queue (the queue will survive a server restart)
         * @param exclusive true if we are declaring an exclusive queue (restricted to this connection)
         * @param autoDelete true if we are declaring an autodelete queue (server will delete it when no longer in use)
         * @param arguments other properties (construction arguments) for the queue
         * @return a declaration-confirm method to indicate the queue was successfully declared
         * @throws java.io.IOException if an error is encountered
         */
        channel.queueDeclare("test001",true,false,false,null);

        DefaultConsumer queueingConsumer = new DefaultConsumer(channel);

        channel.basicConsume("test001",true,queueingConsumer);


        // 关闭连接
        channel.close();
        connection.close();

    }
}
