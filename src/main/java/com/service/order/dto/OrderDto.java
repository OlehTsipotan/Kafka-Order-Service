package com.service.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderDto {

    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;

    @Valid
    @NotNull(message = "Product cannot be null")
    private ProductDto productDto;

    @NotNull(message = "Source cannot be null")
    @Size(min = 1, max = 50, message = "Source must be between 1 and 50 characters")
    private String source;
}
