package com.service.order.controller;

import com.service.order.dto.OrderDto;
import com.service.order.model.Order;
import com.service.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order create(@RequestBody OrderDto orderDto) {
        log.info("Received: " + orderDto);
        return orderService.create(orderDto);
    }
}
