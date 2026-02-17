package com.device.service.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResourceExceptionHandler {

  @ExceptionHandler(DeviceNotFoundException.class)
  public ResponseEntity<StandardError> deviceNotFoundException(DeviceNotFoundException e, HttpServletRequest request){
    StandardError err = new StandardError(System.currentTimeMillis(), HttpStatus.NOT_FOUND.value(),
        e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
  }

  @ExceptionHandler(DeviceInUseException.class)
  public ResponseEntity<StandardError> deviceInUseException(DeviceInUseException e, HttpServletRequest request){
    StandardError err = new StandardError(System.currentTimeMillis(), HttpStatus.BAD_REQUEST.value(),
        e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationError> methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request){
    List<ValidationError.FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
        .map(fe -> new ValidationError.FieldError(fe.getField(), fe.getDefaultMessage()))
        .toList();
    
    ValidationError err = new ValidationError(
        System.currentTimeMillis(),
        HttpStatus.BAD_REQUEST.value(),
        "Validation error",
        request.getRequestURI(),
        fieldErrors
    );
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }

}
