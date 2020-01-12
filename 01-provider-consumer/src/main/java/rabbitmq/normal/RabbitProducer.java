package rabbitmq.normal;


import com.rabbitmq.client.*;


/**
 * @description: 简单的生产者消费者使用
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
        // 参数四：是否自动删除。前提是至少有一个队列或者交换器与这个交换器绑定 ，之后
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
        String message = "Hello World，时间：" + System.currentTimeMillis();
        // 这里可以构造很多参数
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()
                .contentType("text/plain")
                // 2 为持久化
                .deliveryMode(2)
                .build();
        // 发送一条持久化的消息
        channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY, basicProperties,message.getBytes());
        //关闭资源
        channel.close() ;
        connection.close ();
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
