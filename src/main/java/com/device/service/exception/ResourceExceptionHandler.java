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

  /**
   * Handles domain-specific not-found exceptions and returns a 404 error payload.
   * @param e thrown exception
   * @param request current HTTP request
   * @return 404 response containing a standardized error body
   */
  @ExceptionHandler(DeviceNotFoundException.class)
  public ResponseEntity<StandardError> deviceNotFoundException(DeviceNotFoundException e, HttpServletRequest request){
    StandardError err = new StandardError(System.currentTimeMillis(), HttpStatus.NOT_FOUND.value(),
        e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
  }

  /**
   * Handles attempts to modify or delete IN_USE devices and returns a 400 error payload.
   * @param e thrown exception
   * @param request current HTTP request
   * @return 400 response with details about the business rule violation
   */
  @ExceptionHandler(DeviceInUseException.class)
  public ResponseEntity<StandardError> deviceInUseException(DeviceInUseException e, HttpServletRequest request){
    StandardError err = new StandardError(System.currentTimeMillis(), HttpStatus.BAD_REQUEST.value(),
        e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }

  /**
   * Handles bean validation errors and returns a structured list of field errors with status 400.
   * @param e the validation exception thrown by Spring MVC on @Valid failures
   * @param request current HTTP request
   * @return 400 response containing a ValidationError with field-level details
   */
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
