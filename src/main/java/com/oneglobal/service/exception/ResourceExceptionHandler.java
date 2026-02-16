package com.oneglobal.service.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
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
  public ResponseEntity<List<StandardError>> methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request){
    List<StandardError> errors = new ArrayList<>();
    for(ObjectError error : e.getBindingResult().getAllErrors()){
      StandardError err = new StandardError(System.currentTimeMillis(), HttpStatus.BAD_REQUEST.value(),
           error.getDefaultMessage(), request.getRequestURI());
      errors.add(err);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }


}
