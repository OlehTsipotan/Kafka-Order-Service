package com.service.order.service;

import com.service.order.converter.OrderDtoToOrderConverter;
import com.service.order.dto.OrderDto;
import com.service.order.model.Order;
import com.service.order.model.OrderStatus;
import com.service.order.validation.NewOrderValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final KafkaOrderProducerService kafkaOrderProducerService;

    private final NewOrderValidator newOrderValidator;

    private final OrderDtoToOrderConverter converter;

    public Order create(@NonNull OrderDto orderDTO) {
        newOrderValidator.validate(orderDTO);
        Order order = converter.convert(orderDTO);

        kafkaOrderProducerService.sendOrder(order);

        return order;
    }
}
