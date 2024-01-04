package com.service.order.handler;

import com.service.order.exception.ServiceException;
import com.service.order.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(RuntimeException.class)
//    @ResponseStatus(HttpStatus.BAD_GATEWAY)
//    public ErrorResponse handleRuntimeException(RuntimeException e) {
//        log.error(e.);
//        return ErrorResponse.builder(e, HttpStatus.BAD_GATEWAY, e.getMessage()).title("Runtime Exception")
//                .property("timestamp", Instant.now()).build();
//    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleServiceException(ServiceException e) {
        return ErrorResponse.builder(e, HttpStatus.SERVICE_UNAVAILABLE, e.getMessage()).title("Service Exception")
                .property("timestamp", Instant.now()).build();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationException(ValidationException e) {
        return ErrorResponse.builder(e, HttpStatus.CONFLICT, e.getMessage()).title("Validation Exception")
                .property("violations", e.getViolations()).property("timestamp", Instant.now()).build();
    }
}
