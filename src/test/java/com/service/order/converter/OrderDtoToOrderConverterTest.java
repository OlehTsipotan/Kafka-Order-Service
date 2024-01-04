package com.service.order.converter;

import com.service.order.avro.converter.OrderToAvroOrderConverter;
import com.service.order.dto.OrderDto;
import com.service.order.dto.ProductDto;
import com.service.order.model.Order;
import com.service.order.model.OrderStatus;
import com.service.order.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderDtoToOrderConverterTest {

    private OrderDtoToOrderConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new OrderDtoToOrderConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenCarDTOIsNull_throwIllegalArgumentException(OrderDto orderDto) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(orderDto));
    }

    @Test
    public void convert_success() {
        UUID uuidToSet = UUID.randomUUID();
        Long customerId = 1L;
        String source = "Source";
        Long productId = 1L;
        Integer productQuantity = 1;
        Long productPrice = 100L;

        ProductDto productDto = new ProductDto();
        productDto.setId(productId);
        productDto.setQuantity(productQuantity);
        productDto.setPrice(productPrice);

        OrderDto order = new OrderDto();
        order.setProductDto(productDto);
        order.setSource(source);
        order.setCustomerId(customerId);

        System.out.println(converter.convert(order));
        assertEquals(1, 2);
    }

}
