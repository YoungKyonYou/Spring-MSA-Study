package com.example.ecoomerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
//유레카 서버로서 동작하기 위해서는 서버의 자격을 줘야하는데 이게 바로 EnableEurekaServer 어노테이션이다.
@EnableEurekaServer
public class EcoomerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcoomerceApplication.class, args);
    }

}
