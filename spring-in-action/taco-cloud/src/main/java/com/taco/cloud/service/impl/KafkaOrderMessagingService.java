package com.taco.cloud.service.impl;

import com.taco.cloud.entity.TacoOrder;
import com.taco.cloud.service.OrderMessagingService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class KafkaOrderMessagingService implements OrderMessagingService {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendMessage(TacoOrder order) {
        kafkaTemplate.sendDefault(order.toString());
    }
}
