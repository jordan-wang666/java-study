//package com.taco.cloud.service.impl;
//
//import com.taco.cloud.entity.TacoOrder;
//import com.taco.cloud.service.OrderMessagingService;
//import org.springframework.amqp.core.MessageProperties;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//
//@Service
//public class RabbitOrderMessagingServiceImpl implements OrderMessagingService {
//
//    @Resource
//    private RabbitTemplate rabbitTemplate;
//
//    @Override
//    public void sendMessage(TacoOrder order) {
//        rabbitTemplate.convertAndSend("tacocloud.order.queue", order,
//                message -> {
//                    MessageProperties props = message.getMessageProperties();
//                    props.setHeader("X_ORDER_SOURCE", "WEB");
//                    return message;
//                });
//    }
//}
//
