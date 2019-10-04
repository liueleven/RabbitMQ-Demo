package rabbitmq.confirmasync;


import com.rabbitmq.client.*;

import java.awt.*;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * @description: 异步处理，发送方确认机制来保证消息正确消费
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

    private static SortedSet confirmSet = new TreeSet();
    /**
     * RabbitMQ 服务端默认端口号为 5672
     */
    private static final int PORT = 5672;

    public static void main(String[] args) throws Exception {

        publisher();

    }

    private static void publisher() throws Exception {
        // 获取连接
        final Connection connection  = getRabbitConnection();
        // 在连接中创建信道
        Channel channel = connection.createChannel();

        // 参数二：创建一个交换器类型为direct，它会把消息路由到那些 BindingKey 和 RoutingKey 完全匹配的队列中。

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true) ;
        // 声明队列
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        // 绑定队列、交换器、和路由键
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY);

        String message = "测试异步，发送方确认机制，时间：" + (System.currentTimeMillis()/1000);
        // 这里可以构造很多参数
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()
                .contentType("text/plain")
                // 2 为持久化
                .deliveryMode(2)
                .build();
        try {
            // 将信道置为 publisher confirm 模式
            channel.confirmSelect();
            // 设置异步回调
            channel.addConfirmListener(new ConfirmListener() {
                /**
                 * 确认
                 * @param deliveryTag
                 * @param multiple
                 * @throws IOException
                 */
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("确认，序号：" + deliveryTag + " multiple: " + multiple);
                    if (multiple) {
                        confirmSet.headSet(deliveryTag-1).clear();
                    } else {
                        confirmSet.remove(deliveryTag);
                    }
                }

                /**
                 * 未确认
                 * @param deliveryTag
                 * @param multiple
                 * @throws IOException
                 */
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("确认，序号：" + deliveryTag + " multiple: " + multiple);
                    if (multiple) {
                        confirmSet.headSet(deliveryTag-1).clear();
                    } else {
                        confirmSet.remove(deliveryTag);
                    }
                }
            });
            int total = 10;
            while (total > 0) {
                // 获取发送的消息的唯一id
                long nextSeqNo = channel.getNextPublishSeqNo();
                // 发送一条持久化的消息
                channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY,basicProperties,message.getBytes());
                confirmSet.add(nextSeqNo);
                total --;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        TimeUnit.SECONDS.sleep(15);
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
