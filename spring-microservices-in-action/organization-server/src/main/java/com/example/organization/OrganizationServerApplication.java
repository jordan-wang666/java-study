package com.example.organization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class OrganizationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrganizationServerApplication.class, args);
    }

}
