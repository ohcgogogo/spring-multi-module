package com.example.appapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableDiscoveryClient
//@EnableAsync  default값으로 적용되는데, SimpleAsyncTaskExecutor를 사용하게되고 스레드 풀에 의한게 아닌 스레드를 만들어내는 역할만 한다. 스레드를 제대로 관리해주지 못한다고함.
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
//    basePackages = {
//        "com.example.rdbservice"
//    }
//)
public class AppApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApiApplication.class, args);
    }
}
