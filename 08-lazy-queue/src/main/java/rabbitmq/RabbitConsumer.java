package rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @date: 2019-10-03 11:43
 * @author: 十一
 */
public class RabbitConsumer {

    private static final String USER_NAME = "guest";
    private static final String PASSWORD = "guest";
    private static final String QUEUE_NAME = "lazy_queue_demo";
    private static final String IP_ADDRESS = "127.0.0.1";
    /**
     * RabbitMQ 服务端默认端口号为 5672
     */
    private static final int PORT = 5672;

    public static void main(String[] args) throws Exception {

        subscriber();

    }

    private static void subscriber() throws Exception {
        // 获取连接
        Connection connection  = getRabbitConnection();
        // 创建信道，每个线程私有一个channel，不能在线程间共享
        final Channel channel = connection.createChannel();
        //  设置客户端最多接收未被 ack 的消息的个数
        channel.basicQos(64);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("exchange info: " + envelope.toString());
                System.out.println("receive msg：" + new String(body));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 确认接收到
                channel.basicAck(envelope.getDeliveryTag(), false);
                // 拒绝消息，如果requeue是false，会把消息立即移除；是true会重新放入队列，以便发给下一个订阅的消费者
//                channel.basicReject(envelope.getDeliveryTag(),false);
            }
        };
        // autoAck: 不自动确认，等消费者显示回复确认后才会将该消息打上删除标记，之后再删除
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME,autoAck,consumer);
        //等待回调函数执行完毕之后 ， 关闭资源
        TimeUnit.SECONDS.sleep(5);
        // 释放资源
        channel.close();
        connection .close();
    }

    private static Connection getRabbitConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(USER_NAME);
        factory.setPassword(PASSWORD);
        Address[] addresses = {new Address(IP_ADDRESS, PORT)};
        Connection connection = null;
        try {
            connection = factory.newConnection(addresses);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
