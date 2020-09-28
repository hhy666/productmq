package com.springmq.config;


import com.springmq.listener.RobbingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Configuration
public class RabbitmqConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitmqConfig.class);

    @Autowired
    private Environment env;

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    /**
     * 单一消费者
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setTxSize(1);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }

    /**
     * 多个消费者
     * @return
     */
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory,connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        factory.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.concurrency",int.class));
        factory.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.max-concurrency",int.class));
        factory.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.prefetch",int.class));
        return factory;
    }


    //TODO:商品抢单消息模型
    //direct交换机
    @Bean
    public DirectExchange robbingExchange(){
        return new DirectExchange(env.getProperty("product.robbing.mq.exchang.name"),true,false);
    }

    //申明队列
    @Bean(name = "robbingQueue")
    public Queue robbingQueue(){
        return new Queue(env.getProperty("product.robbing.mq.queue.name"),true);
    }

    //绑定队列与交换机
    @Bean
    public Binding robbingBinding(){
        return  BindingBuilder.bind(robbingQueue()).to(robbingExchange()).with(env.getProperty("product.robbing.mq.routing.key.name"));
    }

    @Autowired
    private RobbingListener robbingListener;

    //置消费队列监听
    @Bean
    public SimpleMessageListenerContainer listenerContainerRobbing(@Qualifier("robbingQueue") Queue robbingQueue){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        //TODO: 并发配置
        //并发消费者
        container.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.concurrency",int.class));
        //最大并发用户数
        container.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.max-concurrency",int.class));
        //设置预取计数
        container.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.prefetch",int.class));

        //TODO:消息确认机制
        container.setQueues(robbingQueue);
        container.setMessageListener(robbingListener);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        return container;
    }

}
