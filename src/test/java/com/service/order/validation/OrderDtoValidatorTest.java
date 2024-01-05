package com.service.order.validation;

import com.service.order.dto.OrderDto;
import com.service.order.dto.ProductDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
public class OrderDtoValidatorTest {

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();
    private OrderDtoValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new OrderDtoValidator(jakartaValidator);
    }

    @Test
    public void validate_whenCarIsValid() {
        OrderDto orderDto = new OrderDto();
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setPrice(1L);
        productDto.setQuantity(1);
        orderDto.setProductDto(productDto);
        orderDto.setCustomerId(1L);
        orderDto.setSource("someSource");

        assertDoesNotThrow(() -> validator.validate(orderDto));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenCarIsNull_throwIllegalArgumentException(OrderDto orderDto) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(orderDto));
    }
}