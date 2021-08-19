package com.taco.cloud;

import com.taco.cloud.entity.TacoOrder;
import com.taco.cloud.service.JmsOrderMessagingService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class TacoCloudApplicationTests {

    @Resource
    private JmsOrderMessagingService service;

    @Test
    void contextLoads() {

        TacoOrder tacoOrder = new TacoOrder().builder().ccNumber("1111").build();
        service.SendMessage(tacoOrder);
    }
}
