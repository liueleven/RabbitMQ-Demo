package rabbitmq;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: 死信队列
 * @date: 2019-10-03 11:43
 * @author: 十一
 */
public class RabbitProducer {

    private static final String USER_NAME = "guest";
    private static final String PASSWORD = "guest";
    private static final String EXCHANGE_NAME = "dead_exchange_demo";
    private static final String EXCHANGE_NAME2 = "dead_exchange_demo2";
    private static final String ROUTING_KEY2 = "dead_routing_key_demo2";
    private static final String QUEUE_NAME = "dead_queue_demo";
    private static final String QUEUE_NAME2 = "dead_queue_demo2";
    private static final String IP_ADDRESS = "127.0.0.1";
    /**
     * RabbitMQ 服务端默认端口号为 5672
     */
    private static final int PORT = 5672;

    public static void main(String[] args) throws Exception {

        publisher();

    }

    private static void publisher() throws Exception {
        // 获取连接
        Connection connection  = getRabbitConnection();
        // 在连接中创建信道
        Channel channel = connection.createChannel();

        // 参数二：创建一个交换器类型为direct，它会把消息路由到那些 BindingKey 和 RoutingKey 完全匹配的队列中。

        channel.exchangeDeclare(EXCHANGE_NAME2, BuiltinExchangeType.DIRECT, true) ;
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true) ;
        Map<String,Object> args = new HashMap<String, Object>();
        // 添加死信交换器
        // 还可以添加死信交换器的路由键 args.put("x-dead-letter-routing-key" , "dlx-routing-key");
        args.put("x-dead-letter-exchange",EXCHANGE_NAME2);
        // 添加死信交换器路由键
        args.put("x-dead-letter-routing-key",ROUTING_KEY2);
        // 测试死信，设置消息过期时间
        args.put("x-message-ttl",4000);
        // 声明队列
        channel.queueDeclare(QUEUE_NAME,true,false,false,args);
        // 绑定队列、交换器、和路由键
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"");
        // 声明队列
        channel.queueDeclare(QUEUE_NAME2,true,false,false,null);
        // 绑定队列、交换器、和路由键
        channel.queueBind(QUEUE_NAME2,EXCHANGE_NAME2,ROUTING_KEY2);

        String message = "测试死信队列，时间：" + (System.currentTimeMillis()/1000);
        // 这里可以构造很多参数
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()
                .contentType("text/plain")
                // 2 为持久化
                .deliveryMode(2)
                .build();
        // 发送一条持久化的消息
        // 这里的路由键设置为空，这条消息将不能路由到交换器中，会被投递到备份交换器中
        channel.basicPublish(EXCHANGE_NAME,"test_dead_queue_rk",basicProperties,message.getBytes());
        //关闭资源
        channel.close() ;
        connection.close();
    }

    private static Connection getRabbitConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(USER_NAME);
        factory.setPassword(PASSWORD);
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        Connection conn = null;
        try {
            conn = factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
