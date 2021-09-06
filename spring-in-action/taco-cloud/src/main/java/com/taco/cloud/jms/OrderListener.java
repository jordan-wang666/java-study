//package com.taco.cloud.jms;
//
//import com.taco.cloud.entity.TacoOrder;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class OrderListener {
//
//    //    @JmsListener(destination = "tacocloud.order.queue")
//    @KafkaListener(topics = "tacocloud.orders.topic")
//    public void receiveOrder(String order, ConsumerRecord<String, String> record) {
//        log.info("Get taco order:{}", order);
//        log.info("Received from partition {} with timestamp {}",
//                record.partition(), record.timestamp());
//    }
//}
