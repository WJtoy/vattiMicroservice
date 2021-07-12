package com.kkl.kklplus.b2b;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableSwagger2Doc
@SpringBootApplication
@EnableEurekaClient
public class B2BVattiApplication {

    public static void main(String[] args) {
        SpringApplication.run(B2BVattiApplication.class, args);
    }
}
