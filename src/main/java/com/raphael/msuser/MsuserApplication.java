package com.raphael.msuser;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class MsuserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsuserApplication.class, args);
    }
}
