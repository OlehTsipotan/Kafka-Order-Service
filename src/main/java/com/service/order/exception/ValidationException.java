package com.service.order.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ValidationException extends ServiceException {

    private List<FieldViolation> violations = new ArrayList<>();

    public ValidationException(String errorMessage) {
        super(errorMessage);
    }

    public ValidationException(String errorMessage, List<FieldViolation> violations) {
        super(errorMessage);
        this.violations = violations;
    }

}
