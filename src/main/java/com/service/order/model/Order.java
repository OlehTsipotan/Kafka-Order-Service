package com.service.order.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Order {

    public Order(Order order) {
        this.id = order.getId();
        this.customerId = order.getCustomerId();
        this.product = order.getProduct();
        this.status = order.getStatus();
        this.source = order.getSource();
    }

    private UUID id;

    private Long customerId;

    private Product product;

    private OrderStatus status;

    private String source;
}

