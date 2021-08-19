package com.taco.cloud.service.impl;

import com.taco.cloud.entity.TacoOrder;
import com.taco.cloud.service.JmsOrderMessagingService;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class JmsOrderMessagingServiceImpl implements JmsOrderMessagingService {

    @Resource
    private JmsTemplate jmsTemplate;

    @Override
    public void SendMessage(TacoOrder order) {
        jmsTemplate.send(session -> session.createObjectMessage(order));
    }
}
