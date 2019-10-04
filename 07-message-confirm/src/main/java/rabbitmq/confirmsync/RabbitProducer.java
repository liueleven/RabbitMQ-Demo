package rabbitmq.confirmsync;


import com.rabbitmq.client.*;

/**
 * @description: 发送方确认机制来保证消息正确消费
 * @date: 2019-10-03 11:43
 * @author: 十一
 */
public class RabbitProducer {

    private static final String USER_NAME = "guest";
    private static final String PASSWORD = "guest";
    private static final String EXCHANGE_NAME = "publisher_confirm_exchange_demo";
    private static final String ROUTING_KEY = "publisher_confirm_queue_rk";
    private static final String QUEUE_NAME = "publisher_confirm_queue_demo";
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

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true) ;
        // 声明队列
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        // 绑定队列、交换器、和路由键
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY);

        String message = "测试发送方确认机制，时间：" + (System.currentTimeMillis()/1000);
        // 这里可以构造很多参数
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()
                .contentType("text/plain")
                // 2 为持久化
                .deliveryMode(2)
                .build();
        try {
            // 将信道置为 publisher confirmsync 模式
            channel.confirmSelect();
            // 发送一条持久化的消息
            // 这里的路由键设置为空，这条消息将不能路由到交换器中，会被投递到备份交换器中
            channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY,basicProperties,message.getBytes());
           if (!channel.waitForConfirms()) {
               System.out.println("消息发送失败：" + message);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
