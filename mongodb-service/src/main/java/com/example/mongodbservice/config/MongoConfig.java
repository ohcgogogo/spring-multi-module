package com.example.mongodbservice.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.mongodbservice.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Value("${spring.data.mongodb.url}")
    private String url;
    @Value("${spring.data.mongodb.database}")
    private String database;

//    public MongoConfig(
//            @Value("${spring.data.mongodb.url}") String url,
//            @Value("${spring.data.mongodb.database}") String database
//    ) {
//        this.url = url;
//        this.database = database;
//    }

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(url);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public Collection getMappingBasePackages() {
        return Collections.singleton("com.example.mongodbservice");
    }

//    @Bean
//    MongoOperations mongoTemplate(MongoClient mongoClient) {
//        return new MongoTemplate(mongoClient, getDatabaseName());
//    }
}
