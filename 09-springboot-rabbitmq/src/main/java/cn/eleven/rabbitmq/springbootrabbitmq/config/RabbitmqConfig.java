package cn.eleven.rabbitmq.springbootrabbitmq.config;

import cn.eleven.rabbitmq.springbootrabbitmq.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @description: RabbitMQ 配置及初始化
 * @date: 2019-10-25 17:05
 * @author: 十一
 */
@Configuration
@Slf4j
public class RabbitmqConfig {


    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    public static final String EXCHANGE_A = "my-mq-exchange_A";


    public static final String QUEUE_A = "QUEUE_A";

    public static final String ROUTINGKEY_A = "spring-boot-routingKey_A";

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory factory = new CachingConnectionFactory(host,port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setPublisherConfirms(true);
        return factory;
    }

    @Bean
    @Scope("prototype")
    public Consumer consumer() {
        return new Consumer();
    }

    /**
     * 初始化一个队列、交换机，并指定一个消费者监听类
     * @param consumer
     * @return
     * @throws IOException
     */
    @Bean
    public SimpleMessageListenerContainer consumerQueueMessageContainer(Consumer consumer) throws IOException {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        Connection connection = connectionFactory().createConnection();
        //根据当前服务器ip+端口号创建对应备用队列
        String queueName = QUEUE_A + "_" + InetAddress.getLocalHost().getHostAddress()+"_"+port;
        // 创建队列
        connection.createChannel(false).queueDeclare(queueName, true, false, false, null);
        // 绑定交换机
        connection.createChannel(false).queueBind(queueName,EXCHANGE_A,"");
        container.setQueueNames(queueName);
        //消费者个数
        container.setConcurrentConsumers(1);
        container.setExposeListenerChannel(true);
        //设置每个消费者获取的最大的消息数量
        container.setPrefetchCount(1);
        //设置一个监听处理该队列的消费者类
        container.setMessageListener(consumer);
        System.out.println("初始化备用交换机队列监听成功!");
        connection.close();
        return container;
    }
}
