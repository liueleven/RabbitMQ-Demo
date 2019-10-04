# RabbitMQ-Demo
《RabbitMQ实战指南》知识点总结


[《RabbitMQ实战指南》](https://book.douban.com/subject/27591386/)笔记

书中作者提到了一句话：知是行之始，行是知之成。诸君共勉

# 模块介绍
1. 01-provider-consumer，RabbitMQ生产者消费者的简单实用
2. 02-mandatory，消息没有正确投递，设置mandatory参数进行处理
3. 03-alternate-exchange，消息没有正确投递，使用备份交换器进行处理
```$xslt
注意事项：
1. 备份交换器不存在，消息丢失
2. 备份交换器没有绑定队列，消息丢失
3. 备份交换器没有匹配任何队列，消息丢失
4. 备份交换器和参数mandatory一起使用，mandatory参数无效
```
4. 04-message-ttl，设置消息过期时间的两种方案
5. 05-dead-queue，设置死信队列，处理死信问题，死信队列也可以扩展为延迟队列
6. 06-priority-queue，优先级队列，还没测试
7. 07-message-confirm，消息确认的两种机制
8. 08-lazy-queue，惰性队列的使用