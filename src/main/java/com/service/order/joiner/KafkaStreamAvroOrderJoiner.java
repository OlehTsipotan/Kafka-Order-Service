package com.service.order.joiner;

import com.service.avro.model.AvroOrder;
import com.service.avro.model.AvroOrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.springframework.stereotype.Component;

/**
 * Created by olehtsipotan on 07.02.2024.
 */
@Slf4j
@Component
public class KafkaStreamAvroOrderJoiner implements ValueJoiner<AvroOrder, AvroOrder, AvroOrder> {

    @Override
    public AvroOrder apply(AvroOrder paymentOrder, AvroOrder stockOrder) {
        log.info("Processing stock order: {} and payment order: {}", stockOrder, paymentOrder);
        if (stockOrder.getStatus() == AvroOrderStatus.ACCEPT &&
                paymentOrder.getStatus() == AvroOrderStatus.ACCEPT) {
            stockOrder.setStatus(AvroOrderStatus.CONFIRMATION);
        } else if (stockOrder.getStatus() == AvroOrderStatus.REJECT ||
                paymentOrder.getStatus() == AvroOrderStatus.REJECT) {
            stockOrder.setStatus(AvroOrderStatus.ROLLBACK);
        } else {
            log.info("Unexpected status for stock order: {} and payment order: {}", stockOrder, paymentOrder);
            stockOrder.setStatus(AvroOrderStatus.ROLLBACK);
        }
        return stockOrder;
    }
}
