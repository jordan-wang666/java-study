package com.taco.admin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@SpringBootTest
class ServiceAdminApplicationTests {

    @Test
    void contextLoads() {
        BigDecimal bigDecimal = new BigDecimal("3333.2222");
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        System.out.println(decimalFormat.format(bigDecimal));
    }

}
