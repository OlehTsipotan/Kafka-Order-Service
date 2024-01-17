package com.service.order.service;

import com.service.order.converter.OrderToAvroOrderConverter;
import com.domain.avro.model.AvroOrder;
import com.service.order.exception.ServiceException;
import com.service.order.model.Order;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaOrderProducerService {

    private final KafkaTemplate<String, AvroOrder> template;

    private final OrderToAvroOrderConverter converter;

    @Value("${kafka.order.topic}")
    private String topic;

    public void sendOrder(@NonNull Order order) {
        AvroOrder avroOrder = converter.convert(order);
        try {
            this.template.send(topic, String.valueOf(avroOrder.getId()), avroOrder);
            log.info("Order sent to Kafka: {}", avroOrder);
        } catch (KafkaException e) {
            throw new ServiceException("Error sending order to Kafka", e);
        }
    }
}
