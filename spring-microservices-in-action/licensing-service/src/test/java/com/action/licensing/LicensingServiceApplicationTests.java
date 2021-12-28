package com.action.licensing;

import com.action.licensing.model.License;
import com.action.licensing.services.LicenseService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class LicensingServiceApplicationTests {

    @Resource
    private LicenseService licenseService;

    @Test
    void contextLoads() {
        License license = licenseService.getLicense("e254f8c-c442-4ebe-a82a-e2fc1d1ff78a", "f3831f8c-c338-4ebe-a82a-e2fc1d1ff78a");
    }

}
