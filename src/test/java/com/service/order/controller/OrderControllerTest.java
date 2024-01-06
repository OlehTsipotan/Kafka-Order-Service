package com.service.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.order.dto.OrderDto;
import com.service.order.exception.ServiceException;
import com.service.order.exception.ValidationException;
import com.service.order.model.Order;
import com.service.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({OrderController.class})
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void create_whenOrderDtoIsValid_success() throws Exception {
        Order order = new Order();
        OrderDto carDTO = new OrderDto();
        when(orderService.create(any(OrderDto.class))).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(order)));

        verify(orderService).create(carDTO);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void create_whenOrderDtoIsNull_statusIsBadRequest() throws Exception {

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(orderService);
    }

    @Test
    public void create_whenOrderDtoIsInvalid_statusIsBadRequest() throws Exception {
        OrderDto orderDto = new OrderDto();

        when(orderService.create(any(OrderDto.class))).thenThrow(new ValidationException(""));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isConflict());

        verify(orderService).create(orderDto);
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void create_whenOrderServiceThrowsServiceException() throws Exception {
        OrderDto orderDto = new OrderDto();

        when(orderService.create(any(OrderDto.class))).thenThrow(ServiceException.class);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isBadGateway());

        verify(orderService).create(orderDto);
        verifyNoMoreInteractions(orderService);
    }
}
