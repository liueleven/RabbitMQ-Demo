package rabbitmq.returnlistener;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @description: 一定要写注释啊
 * @date: 2020-01-09 10:24
 * @author: 十一
 */
public class Consumer {

    private static final String USER_NAME = "guest";
    private static final String PASSWORD = "guest";
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;


    public static void main(String[] args) throws IOException {
        Connection connection = getRabbitConnection();
        Channel channel = connection.createChannel();

        String exchange = "test_return_exchange";
        String routingKey = "return.#";
        String queueName = "test_return_queue";


        channel.exchangeDeclare(exchange,"topic",true,false,null);

        channel.queueDeclare(queueName,true,false,false,null);

        channel.queueBind(queueName,exchange,routingKey);


        while (true) {

        }
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
