package com.service.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.order.topic}")
    private String orderTopic;

    @Value("${kafka.order.payment.topic}")
    private String paymentOrderTopic;

    @Value("${kafka.order.stock.topic}")
    private String stockOrderTopic;

    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(orderTopic).partitions(3).compact().build();
    }

    @Bean
    public NewTopic orderPaymentTopic() {
        return TopicBuilder.name(paymentOrderTopic).partitions(3).compact().build();
    }

    @Bean
    public NewTopic orderStockTopic() {
        return TopicBuilder.name(stockOrderTopic).partitions(3).compact().build();
    }

}
