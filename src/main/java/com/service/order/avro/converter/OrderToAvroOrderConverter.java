package com.service.order.avro.converter;

import com.service.order.avro.model.AvroOrder;
import com.service.order.model.Order;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OrderToAvroOrderConverter implements Converter<Order, AvroOrder> {

    private final ModelMapper modelMapper;


    public OrderToAvroOrderConverter() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public AvroOrder convert(@NonNull Order source) {
        AvroOrder avroOrder = modelMapper.map(source, AvroOrder.class);
        avroOrder.setId(source.getId().toString());
        return avroOrder;
    }
}
