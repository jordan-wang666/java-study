package com.taco.cloud;

import com.taco.cloud.entity.TacoOrder;
import com.taco.cloud.service.OrderMessagingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
class TacoCloudApplicationTests {

    @Resource
    private OrderMessagingService rabbitOrderMessagingService;

    @Test
    void contextLoads() {
        TacoOrder tacoOrder = TacoOrder.builder().ccNumber("1111").build();
        rabbitOrderMessagingService.sendMessage(tacoOrder);

//        try {
//            TacoOrder tacoOrder = service.receiveOrder();
//            log.info("tacoOrder:{}", tacoOrder);
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
    }
}
