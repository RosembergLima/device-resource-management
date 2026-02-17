package com.device.service.exception;

public class DeviceInUseException extends RuntimeException {

  public DeviceInUseException(String message) {
    super(message);
  }
}
