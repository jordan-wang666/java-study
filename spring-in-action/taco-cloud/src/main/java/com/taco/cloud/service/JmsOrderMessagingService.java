package com.taco.cloud.service;

import com.taco.cloud.entity.TacoOrder;

import javax.jms.JMSException;

public interface JmsOrderMessagingService {
    void sendMessage(TacoOrder order);

    TacoOrder receiveOrder() throws JMSException;
}
