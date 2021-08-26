package com.taco.cloud.service;

import com.taco.cloud.entity.TacoOrder;


public interface OrderMessagingService {

    void sendMessage(TacoOrder order);

//    TacoOrder receiveOrder() throws JMSException;
}
