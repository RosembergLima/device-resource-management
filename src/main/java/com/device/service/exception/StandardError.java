package com.device.service.exception;

public record StandardError(Long timeStamp, Integer status, String message, String path) {

}
