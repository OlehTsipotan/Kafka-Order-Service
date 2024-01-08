package com.service.order.service;

import com.service.order.converter.OrderDtoToOrderConverter;
import com.service.order.dto.OrderDto;
import com.service.order.exception.ServiceException;
import com.service.order.exception.ValidationException;
import com.service.order.model.Order;
import com.service.order.validation.OrderDtoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private KafkaOrderProducerService kafkaOrderProducerService;

    @Mock
    private OrderDtoValidator orderDtoValidator;

    @Mock
    private OrderDtoToOrderConverter converter;

    @BeforeEach
    public void setUp() {
        orderService = new OrderService(kafkaOrderProducerService, orderDtoValidator, converter);
    }

    @Test
    public void create_success() {
        OrderDto orderDto = new OrderDto();
        Order converterOrder = new Order();

        when(converter.convert(orderDto)).thenReturn(converterOrder);

        orderService.create(orderDto);

        Mockito.verify(kafkaOrderProducerService).sendOrder(converterOrder);
        Mockito.verify(orderDtoValidator).validate(orderDto);
        Mockito.verify(converter).convert(orderDto);

        Mockito.verifyNoMoreInteractions(kafkaOrderProducerService, orderDtoValidator, converter);
    }

    @ParameterizedTest
    @NullSource
    public void create_whenOrderDtoIsNull_throwIllegalArgumentException(OrderDto nullOrderDto) {
        assertThrows(IllegalArgumentException.class, () -> orderService.create(nullOrderDto));

        Mockito.verifyNoInteractions(kafkaOrderProducerService, orderDtoValidator, converter);
    }

    @Test
    public void create_whenOrderDtoValidatorThrowsValidationException_throwValidationException() {
        OrderDto orderDto = new OrderDto();

        Mockito.doThrow(ValidationException.class).when(orderDtoValidator).validate(orderDto);

        assertThrows(ValidationException.class, () -> orderService.create(orderDto));

        Mockito.verify(orderDtoValidator).validate(orderDto);
        Mockito.verifyNoMoreInteractions(orderDtoValidator);
        Mockito.verifyNoInteractions(kafkaOrderProducerService, converter);
    }

    @Test
    public void create_whenKafkaOrderProducerServiceThrowsServiceException_throwServiceException() {
        OrderDto orderDto = new OrderDto();
        Order converterOrder = new Order();

        when(converter.convert(orderDto)).thenReturn(converterOrder);
        Mockito.doThrow(ServiceException.class).when(kafkaOrderProducerService).sendOrder(converterOrder);

        assertThrows(ServiceException.class, () -> orderService.create(orderDto));

        Mockito.verify(kafkaOrderProducerService).sendOrder(converterOrder);
        Mockito.verify(orderDtoValidator).validate(orderDto);
        Mockito.verify(converter).convert(orderDto);

        Mockito.verifyNoMoreInteractions(kafkaOrderProducerService, orderDtoValidator, converter);
    }


}
