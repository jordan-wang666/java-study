package com.taco.cloud.jms;

import com.taco.cloud.entity.TacoOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderListener {

//    @JmsListener(destination = "tacocloud.order.queue")
    public void receiveOrder(TacoOrder order) {
        log.info("Get taco order:{}", order);
    }
}
