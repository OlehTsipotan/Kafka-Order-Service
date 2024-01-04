package com.service.order.avro.converter;

import com.service.order.model.Order;
import com.service.order.model.OrderStatus;
import com.service.order.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.text.spi.CollatorProvider;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderToAvroOrderConverterTest {

    private OrderToAvroOrderConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new OrderToAvroOrderConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenCarDTOIsNull_throwIllegalArgumentException(Order order) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(order));
    }

    @Test
    public void convert_success() {
        UUID uuidToSet = UUID.randomUUID();
        Long customerId = 1L;
        String source = "Source";
        Long productId = 1L;
        Integer productQuantity = 1;
        Long productPrice = 100L;

        Product product = new Product();
        product.setId(productId);
        product.setQuantity(productQuantity);
        product.setPrice(productPrice);

        Order order = new Order();
        order.setId(uuidToSet);
        order.setStatus(OrderStatus.NEW);
        order.setProduct(product);
        order.setSource(source);
        order.setCustomerId(customerId);

        System.out.println(converter.convert(order));
        assertEquals(1, 2);
    }

}
