package com.taco.cloud;

import com.taco.cloud.entity.TacoOrder;
import com.taco.cloud.jms.OrderListener;
import com.taco.cloud.service.JmsOrderMessagingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.jms.JMSException;

@Slf4j
@SpringBootTest
class TacoCloudApplicationTests {

    @Resource
    private JmsOrderMessagingService service;

    @Test
    void contextLoads() {

        TacoOrder tacoOrder = new TacoOrder().builder().ccNumber("1111").build();
        service.sendMessage(tacoOrder);

//        try {
//            TacoOrder tacoOrder = service.receiveOrder();
//            log.info("tacoOrder:{}", tacoOrder);
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
    }
}
