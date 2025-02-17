package com.example.appconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

//@EnableAsync
@SpringBootApplication
@EntityScan({"com.example.rdbservice","com.example.elasticsearchservice"})
@ComponentScans({
        @ComponentScan(basePackages = "com.example.businessservice"),
        @ComponentScan(basePackages = "com.example.rdbservice"),
        @ComponentScan(basePackages = "com.example.kafkaservice"),
        @ComponentScan(basePackages = "com.example.elasticsearchservice"),
        @ComponentScan(basePackages = "com.example.rabbitmqservice"),
        @ComponentScan(basePackages = "com.example.mongodbservice")
})
//@EnableJpaRepositories(
//        basePackages = {"com.example.rdbservice"}
//)
public class AppConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppConsumerApplication.class, args);
    }
}
