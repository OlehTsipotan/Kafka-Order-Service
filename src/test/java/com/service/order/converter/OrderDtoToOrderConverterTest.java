package com.service.order.converter;

import com.service.order.dto.OrderDto;
import com.service.order.dto.ProductDto;
import com.service.order.model.Order;
import com.service.order.model.OrderStatus;
import com.service.order.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrderDtoToOrderConverterTest {

    private OrderDtoToOrderConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new OrderDtoToOrderConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenOrderDtoIsNull_throwIllegalArgumentException(OrderDto orderDto) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(orderDto));
    }

    @Test
    public void convert_whenOrderDtoFieldsAreNull_success() {
        OrderDto orderDto = new OrderDto();

        Order order = converter.convert(orderDto);

        assertNotNull(order);
        Product product = order.getProduct();
        assertNull(product);
        assertEquals(orderDto.getCustomerId(), order.getCustomerId());
        assertEquals(orderDto.getSource(), order.getSource());
    }

    @Test
    public void convert_success() {
        Long productDtoId = 1L;
        Integer productDtoQuantity = 1;
        Long productDtoPrice = 1L;

        ProductDto productDto = new ProductDto();
        productDto.setId(productDtoId);
        productDto.setQuantity(productDtoQuantity);
        productDto.setPrice(productDtoPrice);

        String orderDtoSource = "someSource";
        Long orderDtoCustomerId = 1L;

        OrderDto orderDto = new OrderDto();
        orderDto.setProductDto(productDto);
        orderDto.setSource(orderDtoSource);
        orderDto.setCustomerId(orderDtoCustomerId);

        Order order = converter.convert(orderDto);
        assertNotNull(order);

        Product product = order.getProduct();
        assertNotNull(product);

        assertNotNull(order.getId());
        assertEquals(order.getCustomerId(), orderDtoCustomerId);
        assertEquals(order.getStatus(), OrderStatus.NEW);
        assertEquals(product.getId(), productDtoId);
        assertEquals(product.getQuantity(), productDtoQuantity);
        assertEquals(product.getPrice(), productDtoPrice);
        assertEquals(order.getSource(), orderDtoSource);
    }

}
