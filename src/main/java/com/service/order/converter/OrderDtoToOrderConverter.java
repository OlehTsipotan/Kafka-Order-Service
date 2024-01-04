package com.service.order.converter;

import com.service.order.dto.OrderDto;
import com.service.order.model.Order;
import com.service.order.model.OrderStatus;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderDtoToOrderConverter implements Converter<OrderDto, Order> {

    private final ModelMapper modelMapper;


    public OrderDtoToOrderConverter() {
        this.modelMapper = new ModelMapper();
        // Otherwise OrderDto.customerId is mapping to Order.id
        modelMapper.typeMap(OrderDto.class, Order.class).addMappings(mapper -> mapper.skip(Order::setId));
    }

    @Override
    public Order convert(@NonNull OrderDto source) {
        Order order = modelMapper.map(source, Order.class);

        order.setStatus(OrderStatus.NEW);
        order.setId(UUID.randomUUID());

        return order;
    }
}
