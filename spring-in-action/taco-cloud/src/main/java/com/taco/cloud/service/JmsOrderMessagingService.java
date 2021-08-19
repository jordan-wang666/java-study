package com.taco.cloud.service;

import com.taco.cloud.entity.TacoOrder;

public interface JmsOrderMessagingService {
    void SendMessage(TacoOrder order);
}
