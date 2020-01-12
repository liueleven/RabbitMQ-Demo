package rabbitmq.returnlistener;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @description: 一定要写注释啊
 * @date: 2020-01-09 10:24
 * @author: 十一
 */
public class Producer {

    private static final String USER_NAME = "guest";
    private static final String PASSWORD = "guest";
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;


    public static void main(String[] args) throws IOException {
        Connection connection = getRabbitConnection();
        Channel channel = connection.createChannel();

        String exchange = "test_return_exchange";
        String routingKey = "return.save";
        String routingKeyError = "abc.save";

        String msg = "Hello RabbitMQ Return Message";

        channel.addReturnListener(new ReturnListener() {
            public void handleReturn(int replyCode, String replyText, String exchange
                    , String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("============================");
                System.out.println("replyCode: "+replyCode);
                System.out.println("replyText: "+replyText);
                System.out.println("exchange: "+exchange);
                System.out.println("routingKey: "+routingKey);
                System.out.println("properties: "+properties);
                System.out.println("body: "+new String(body));

            }
        });

        channel.basicPublish(exchange,routingKey,true,null,msg.getBytes());
    }











    private static Connection getRabbitConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(USER_NAME);
        factory.setPassword(PASSWORD);
        factory.setVirtualHost("/");
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
