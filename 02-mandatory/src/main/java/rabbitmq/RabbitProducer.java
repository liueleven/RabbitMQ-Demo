package rabbitmq;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @description: 消息没有正确投递，会退回来
 *               概括：mandatory 参数告诉服务器至少将该消息路由到一个队列中，否则将消息返回给生产者，可以
 *                              设置addReturnListener中的回退业务逻辑，如果未设置mandatory参数，该消息会被丢弃
 *
 *                    immediate 参数告诉服务器,如果该消息关联的队列上有消费者,则立刻投递；否则，则直接
 *                              将消息返还给生产者，不用将消息存入队列而等待消费者了。该参数在3.0后就不用了
 * @date: 2019-10-03 11:43
 * @author: 十一
 */
public class RabbitProducer {

    private static final String USER_NAME = "guest";
    private static final String PASSWORD = "guest";
    private static final String EXCHANGE_NAME = "exchange_demo";
    private static final String ROUTING_KEY = "routingkey_demo";
    private static final String QUEUE_NAME = "queue_demo";
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
        // 参数一：交换器名称
        // 参数二：创建一个 type="direct"
        // 参数三：是否持久化
        // 参数四：是否自动删除。前提是至少有一个队列或者交换器与这个交换器绑定，之后
        //       所有与这个交换器绑定的队列或者交换器都与此解绑，才会删除
        // 参数五：其它需要设置的参数
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, null) ;
        // 参数一：自定义队列名称
        // 参数二：是否持久化
        // 参数三：是否排他。特点：1.该连接断开，自动删除；2.同一连接的不同信道是可以访问的；3.其它连接不允许创建同名队列
        //        适用于一个客户端同时发送和读取消息的应用场景。
        // 参数四：是否自动删除。前提是至少有一个消费者连接到这个队列，之后
        //        所有与这个队列连接的消费者都断开时，才会自动删除
        // 参数五：其它需要设置的参数
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        // 参数一：自定义队列名称
        // 参数二：交换器名称
        // 参数三：用来绑定队列和交换器的路由键
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY);
        String message = "Hello World，测试消息没有正确被投递，时间：" + System.currentTimeMillis();
        // 这里可以构造很多自定义参数
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()
                .contentType("text/plain")
                // 2 为持久化
                .deliveryMode(2)
                .build();
        // 发送一条持久化的消息
        // mandatory为true，这里的路由键设置为空，这条消息将不能路由到交换器中
        boolean mandatory = true;
        channel.basicPublish(EXCHANGE_NAME,"",mandatory,basicProperties,message.getBytes());
        channel.addReturnListener(new ReturnListener() {
            public void handleReturn(int replyCode, String replyText, String exchange
                    , String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);
                System.out.println("消息没有正确投递，退回来了：" + msg);
            }
        });
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
