package com.springmq.listener;

import com.rabbitmq.client.Channel;
import com.springmq.config.RabbitmqConfig;
import com.springmq.service.ConcurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareBatchMessageListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("robbingListener")
public class RobbingListener implements ChannelAwareMessageListener {

    private static final Logger log = LoggerFactory.getLogger(RobbingListener.class);

    @Autowired
    private ConcurrencyService concurrencyService;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        Long tag = message.getMessageProperties().getDeliveryTag();
        try{
            byte[] body = message.getBody();
            String mobile = new String(body,"UTF-8");
            //抢单消息业务逻辑
            concurrencyService.manageRobbing(mobile);
            //手动确认消息
            channel.basicAck(tag,true);
        }catch (Exception e){
            //发生异常，拒绝确认
            //第三个参数，冲回队列
            channel.basicNack(tag,true,true);
            log.error("监听的消费消息 无发生异常:{}",e.fillInStackTrace());
        }
    }
}
