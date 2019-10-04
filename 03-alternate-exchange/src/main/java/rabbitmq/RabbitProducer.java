package rabbitmq;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: 测试备份交换器，消息不能正确投递，会将消费发送到备份交换器中
 * @date: 2019-10-03 11:43
 * @author: 十一
 */
public class RabbitProducer {

    private static final String USER_NAME = "guest";
    private static final String PASSWORD = "guest";
    private static final String EXCHANGE_NAME = "normal_exchange_demo_2";
    private static final String ROUTING_KEY = "routing_key_demo_2";
    private static final String QUEUE_NAME = "normal_queue_demo_2";
    private static final String QUEUE_NAME2 = "unrouting_queue_demo_2";
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
        Map<String,Object> args = new HashMap<String, Object>();
        String alternateExchangeName = "myAE";
        args.put("alternate-exchange",alternateExchangeName);
        // 参数二：创建一个交换器类型为direct，它会把消息路由到那些 BindingKey 和 RoutingKey 完全匹配的队列中。
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, args) ;
        // 声明队列
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        // 绑定队列、交换器、和路由键
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY);

        // 创建一个交换器类型为 fanout，它会把所有发送到该交换器的消息路由到所有与该交换器绑定的队列中。
        channel.exchangeDeclare(alternateExchangeName,BuiltinExchangeType.FANOUT,true,false,null);
        channel.queueDeclare(QUEUE_NAME2,true,false,false,null);
        channel.queueBind(QUEUE_NAME2,alternateExchangeName,"");

        String message = "Hello World，时间：" + System.currentTimeMillis();
        // 这里可以构造很多参数
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()
                .contentType("text/plain")
                // 2 为持久化
                .deliveryMode(2)
                .build();
        // 发送一条持久化的消息
        // 这里的路由键设置为空，这条消息将不能路由到交换器中，会被投递到备份交换器中
        channel.basicPublish(EXCHANGE_NAME,"",basicProperties,message.getBytes());
        TimeUnit.SECONDS.sleep(10);
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
