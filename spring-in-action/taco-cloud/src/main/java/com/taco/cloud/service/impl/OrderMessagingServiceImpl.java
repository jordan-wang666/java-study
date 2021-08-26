//package com.taco.cloud.service.impl;
//
//import com.taco.cloud.entity.TacoOrder;
//import com.taco.cloud.service.OrderMessagingService;
//import org.springframework.stereotype.Service;
//
//
//@Service
//public class OrderMessagingServiceImpl implements OrderMessagingService {
//
////    @Resource
////    private JmsTemplate jmsTemplate;
//
////    @Resource
////    private MessageConverter messageConverter;
//
//    @Override
//    public void sendMessage(TacoOrder order) {
////        jmsTemplate.send(session -> session.createObjectMessage(order));
////        jmsTemplate.convertAndSend("tacocloud.order.queue", order, message -> {
////            message.setStringProperty("X_ORDER_SOURCE", "WEB");
////            return message;
////        });
//    }
//
////    @Override
////    public TacoOrder receiveOrder() throws JMSException {
//////        Message receive = jmsTemplate.receive("tacocloud.order.queue");
//////        assert receive != null;
//////        return (TacoOrder) messageConverter.fromMessage(receive);
////        return (TacoOrder) jmsTemplate.receiveAndConvert("tacocloud.order.queue");
////    }
//}
