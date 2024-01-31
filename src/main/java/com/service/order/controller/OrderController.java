package com.service.order.controller;

import com.service.order.dto.OrderDto;
import com.service.order.model.Order;
import com.service.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create the Order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))})})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody OrderDto orderDto) {
        log.info("Received: " + orderDto);
        return orderService.create(orderDto);
    }
}
