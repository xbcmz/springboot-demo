package com.haoxs.delay.rabbitmq.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@Slf4j
@AllArgsConstructor
public class RabbitMqConfig {

    /**
     * 使用json序列化机制，进行消息转换
     *
     * @return
     */
    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定义交换机，订单业务统一使用 order.exchange 交换机
     */
    @Bean
    public Exchange exchange() {
        return new TopicExchange("order.exchange", true, false);
    }

    /**
     * 延时队列（超时会通过交换机和路由key转发到死信队列），没有消费者
     */
    @Bean
    public Queue delayQueue() {
        // 延时队列的消息过期了，会自动触发消息的转发，根据routingKey发送到指定的exchange中，exchange路由到死信队列
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "order.exchange");
        args.put("x-dead-letter-routing-key", "order:close"); // 死信路由Key
        args.put("x-message-ttl", 60000); // 单位：毫秒，1分钟测试使用
        return new Queue("order.delay.queue", true, false, false, args);
    }


    /**
     * 延时队列绑定交换机，路由键order.create
     * 订单提交时会发送routingKey=order.create.order的消息至exchange，然后会被路由到上面的delayQueue延时队列，延时队列没有消费者，到期后会将消息转发
     */
    @Bean
    public Binding delayQueueBinding() {
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE, "order.exchange", "order.create", null);
    }

    /**
     * 死信队列（普通队列）
     */
    @Bean
    public Queue closeOrderQueue() {
        return new Queue("order.close.queue", true, false, false);
    }

    /**
     * 死信队列绑定交换机
     * 其中死信路由的routingKey=order:close和延时队列的routingKey一致，延时队列过期时将消息发送给exchange，exchange再路由到死信队列
     */
    @Bean
    public Binding closeOrderQueueBinding() {
        return new Binding("order.close.queue", Binding.DestinationType.QUEUE, "order.exchange", "order:close", null);
    }

}
