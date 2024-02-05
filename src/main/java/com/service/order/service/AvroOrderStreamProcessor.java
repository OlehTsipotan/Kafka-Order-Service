package com.service.order.service;

import com.service.avro.model.AvroOrder;
import com.service.avro.model.AvroOrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by olehtsipotan on 03.02.2024.
 */
@Service
@Slf4j
public class AvroOrderStreamProcessor {

    public AvroOrder process(AvroOrder stockOrder, AvroOrder paymentOrder) {
        log.info("Processing stock order: {} and payment order: {}", stockOrder, paymentOrder);
        if (stockOrder.getStatus() == AvroOrderStatus.ACCEPT &&
                paymentOrder.getStatus() == AvroOrderStatus.ACCEPT) {
            stockOrder.setStatus(AvroOrderStatus.CONFIRMATION);
        } else if (stockOrder.getStatus() == AvroOrderStatus.REJECT &&
                paymentOrder.getStatus() == AvroOrderStatus.REJECT) {
            stockOrder.setStatus(AvroOrderStatus.ROLLBACK);;
        } else {
            log.info("Unexpected status for stock order: {} and payment order: {}", stockOrder, paymentOrder);
            stockOrder.setStatus(AvroOrderStatus.ROLLBACK);
        }
        return stockOrder;
    }


}
