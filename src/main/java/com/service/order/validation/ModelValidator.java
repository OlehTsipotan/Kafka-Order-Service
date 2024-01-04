package com.service.order.validation;

import com.service.order.exception.FieldViolation;
import com.service.order.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public abstract class ModelValidator<T> {

    private final Validator validator;

    public void validate(T t) {
        Set<ConstraintViolation<Object>> violations = validator.validate(t);
        if (!violations.isEmpty()) {
            throw new ValidationException(String.format("Validation error with %s", t.toString()), violations.stream()
                    .map((constraintViolation) -> new FieldViolation(constraintViolation.getPropertyPath().toString(),
                            constraintViolation.getRootBeanClass().getSimpleName(),
                            constraintViolation.getInvalidValue(), constraintViolation.getMessage())).toList());
        }

    }
}
