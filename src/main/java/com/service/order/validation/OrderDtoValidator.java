package com.service.order.validation;

import com.service.order.exception.FieldViolation;
import com.service.order.exception.ValidationException;
import com.service.order.dto.OrderDto;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class OrderDtoValidator extends ModelValidator<OrderDto> {


    public OrderDtoValidator(Validator validator) {
        super(validator);
    }

    @Override
    public void validate(OrderDto orderDTO) {
        List<FieldViolation> violations = new ArrayList<>();
        try {
            super.validate(orderDTO);
        } catch (ValidationException e) {
            violations = e.getViolations();
        }

        if (!violations.isEmpty()) {
            log.info(violations.toString());
            throw new ValidationException("NewOrder is not valid", violations);
        }

    }
}
