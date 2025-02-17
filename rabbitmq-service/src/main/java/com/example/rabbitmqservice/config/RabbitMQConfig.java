package com.example.rabbitmqservice.config;

import com.example.rabbitmqservice.event.RabbitMqSignupEventProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

// https://docs.spring.io/spring-amqp/reference/html/
@Slf4j
@Configuration
public class RabbitMQConfig {
    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitmqPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

//    @Value("${rabbitmq.queue}")
//    private String queueName;
//
//    @Value("${rabbitmq.exchange}")
//    private String exchangeName;
//
//    @Value("${rabbitmq.routing}")
//    private String routingKey;

    @Bean
    public Queue queue01() {
        return QueueBuilder
                .durable(RabbitMqSignupEventProperties.QUEUE)
                .deadLetterExchange(RabbitMqSignupEventProperties.DEAD_LETTER_EXCHANGE)
                .build();
//        return new Queue(queueName, true);
    }
    @Bean
    public Queue deadLetterQueue01() {
        return new Queue(RabbitMqSignupEventProperties.DEAD_LETTER_QUEUE, true);
    }
    @Bean
    public Queue queue02() {
        return new Queue("queue-02", true);
    }


    @Bean
    public DirectExchange exchange01() { return new DirectExchange(RabbitMqSignupEventProperties.EXCHANGE); }
    @Bean
    public FanoutExchange deadLetterExchange01() { return new FanoutExchange(RabbitMqSignupEventProperties.DEAD_LETTER_EXCHANGE); }
    @Bean
    public DirectExchange exchange02() {
        return new DirectExchange("exchange-02");
    }


    @Bean
    public Binding binding01(Queue queue01, DirectExchange exchange01) {
        return BindingBuilder.bind(queue01).to(exchange01).with(RabbitMqSignupEventProperties.ROUTING);
    }
    @Bean
    public Binding deadLetterBinding01(Queue deadLetterQueue01, FanoutExchange deadLetterExchange01) {
        return BindingBuilder.bind(deadLetterQueue01).to(deadLetterExchange01);
    }
    @Bean
    public Binding binding02(Queue queue02, DirectExchange exchange02) {
        return BindingBuilder.bind(queue02).to(exchange02).with("routing_key_02");
    }


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqHost);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // JSON 형식의 메시지를 직렬화하고 역직렬할 수 있도록 설정
//        rabbitTemplate.setMandatory(true);
//        rabbitTemplate.setChannelTransacted(true);
        rabbitTemplate.setRetryTemplate(new RetryTemplate());
        rabbitTemplate.setReplyTimeout(10000);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                Message message = correlationData.getReturned().getMessage();
                byte[] body = message.getBody();
                log.info("Fail to produce. ID: {} {}", correlationData.getId(), cause);
            }
        }); // consumer에서 처리한 결과를 callback queue를 통해 받아 처리할 로직을 작성 (routing 실패 보고)
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDefaultRequeueRejected(true); // 예외발생 시 다시 큐에 넣을지
        factory.setMessageConverter(jackson2JsonMessageConverter());
//        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO); // 처리 여부를 자동으로 받을지
//        factory.setErrorHandler(new ConditionalRejectingErrorHandler(
//            t -> t instanceof ListenerExecutionFailedException
//        ));
//        factory.setErrorHandler(new ConditionalRejectingErrorHandler());
//        factory.setChannelTransacted(true);
        factory.setAdviceChain(
            RetryInterceptorBuilder
                .stateless()
                .maxAttempts(3) // 예외 발생 시 재시도 횟수
                .recoverer(new RejectAndDontRequeueRecoverer()) // 재시도시 사용
                .backOffOptions(3L, 3, 10L) // 재시도 횟수에 대한 옵션, 간격, 횟수, 재시도종료시간
                .build());
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory deadLetterContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }
}
